package com.ericsson.fms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.BatchcreateResult;
import com.ericsson.fms.entity.EsBean;
import com.ericsson.fms.entity.Location;
import com.ericsson.fms.entity.VehiclesResponse;
import com.ericsson.fms.entity.vehicle.VehiclePolygon;
import com.ericsson.fms.entity.vehicle.VehiclePosition;
import com.ericsson.fms.entity.vehicle.VehiclePostionTime;
import com.ericsson.fms.exception.http.*;
import com.ericsson.fms.utils.HttlUtil;
import com.ericsson.fms.utils.IndexCalculationUtil;
import com.ericsson.fms.utils.JsonObject;
import com.ericsson.fms.utils.StringUtil;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ElasticSearchService{
    public static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
    @Value("${spring.data.elasticsearch.cluster-nodes}") //获取集群节点
    private String clusterNodes;

    @Value("${spring.data.elasticsearch.cluster-name}")//获取集群名称
    private String elasticClusterName;

	@Value("${timestamp.format}")
	private String timeStampFormatString;

	@Value("${search.time.interval}")
	private String searchTimeInterval;

	@Value("${search.vinvrn.limit}")
	private int searchLimit;

    private TransportClient client;
    
    private static final String GIS_DATA_TYPE = "gisdata";
	private static final String EXACT = "EXACT";

	private static final int MAX_SIZE = 2147483641;

	private static final int SEARCH_SIZE = 50;
	private static final Integer ES_PORT = 9200;
	private static final String ES_HTTP = "http://";

	private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	private SimpleDateFormat timeStampFormat;

    @PostConstruct
    public void init()throws Exception {

		timeStampFormat = new SimpleDateFormat(timeStampFormatString);
		timeStampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		searchTimeInterval = searchTimeInterval == null ? "3" : searchTimeInterval;

    	Settings settings = Settings.builder()
                .put("cluster.name", elasticClusterName)
                .put("client.transport.sniff", true).build();

        try {
            PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(settings);
            logger.info("clusterNodes:" + clusterNodes);
            if (!"".equals(clusterNodes)) {
                for (String nodes : clusterNodes.split(",")) {
                    String InetSocket[] = nodes.split(":");
                    String address = InetSocket[0];
                    Integer port = Integer.valueOf(InetSocket[1]);
                    preBuiltTransportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), port));
                }
                client = preBuiltTransportClient;

				updateOldIndexMapping(getIndexs(),getUpdateDate());
                logger.info("client" + client);
            }
        } catch (UnknownHostException e) {
            logger.warn(e.getMessage());
        }

    }

//    @PreDestroy
//    public void destroy() {
//        if (client != null) {
//            client.close();
//        }
//    }

	public boolean isIndexExists(String index) {
		boolean indexIsExist = false;
		if (StringUtil.isEmpty(index)) {
			logger.info("index is null or empty:" + index);
		} else {
			indexIsExist = client.admin().indices().prepareExists(index).execute().actionGet().isExists();
			logger.info("index" + index + " is existed:" + indexIsExist);
		}
		return indexIsExist;
	}

    /**
	 * search
	 * must： AND
	 * must_not：NOT
	 * should：OR
	 * Range:datetime
	 */
	public List<Location> search(String index,Integer precision,GeoPoint topLeft,GeoPoint bottomRight, Map<String, Object[]> mustCondition, Map<String, Object[]> mustNotCondition, Map<String, Object[]> shouldCondition, Map<String, Object[]> rangeCondition,Map<String, Object[]> inCondition) throws HttpException{
		List<Location> list = new ArrayList<Location>();
		try{
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(GIS_DATA_TYPE).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
			if (mustCondition != null && !mustCondition.isEmpty() && mustCondition.size()!=0) {
				for (Map.Entry<String, Object[]> entry : mustCondition.entrySet()) {
					if (entry.getValue() instanceof   Object[] ||entry.getValue() instanceof   String[]){
						for (Object obj:entry.getValue()) {
							queryBuilder.must(QueryBuilders.matchQuery(entry.getKey(), obj));
						}
					}else{
						queryBuilder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
					}
				}
			}

			if (mustNotCondition != null && !mustNotCondition.isEmpty() && mustNotCondition.size()!=0) {
				for (Map.Entry<String, Object[]> entry : mustNotCondition.entrySet()) {
					if (entry.getValue() instanceof   Object[] ||entry.getValue() instanceof   String[]){
						for (Object obj:entry.getValue()) {
							queryBuilder.mustNot(QueryBuilders.matchQuery(entry.getKey(), obj));
						}
					}else{
						queryBuilder.mustNot(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
					}

				}
			}

			if (shouldCondition != null && !shouldCondition.isEmpty() && shouldCondition.size()!=0) {
				for (Map.Entry<String, Object[]> entry : shouldCondition.entrySet()) {
					if (entry.getValue() instanceof   Object[] ||entry.getValue() instanceof   String[]){
						for (Object obj:entry.getValue()) {
							queryBuilder.should(QueryBuilders.matchQuery(entry.getKey(), obj));
						}
					}else{
						queryBuilder.should(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
					}

				}
			}

			if (inCondition != null && !inCondition.isEmpty() && inCondition.size()!=0) {
				for (Map.Entry<String, Object[]> entry : inCondition.entrySet()) {
					QueryBuilder queryBuilderTemp = QueryBuilders.boolQuery();
					if (entry.getValue() instanceof   Object[] ||entry.getValue() instanceof   String[]){
						for (Object obj:entry.getValue()) {
							((BoolQueryBuilder) queryBuilderTemp).should(QueryBuilders.matchPhraseQuery(entry.getKey(), obj).slop(0));
						}
					}
					queryBuilder.must(queryBuilderTemp);
				}
			}

			searchRequestBuilder.setQuery(queryBuilder);
			if (rangeCondition != null && !rangeCondition.isEmpty() && rangeCondition.size()!=0) {
				for (Map.Entry<String, Object[]> entry : rangeCondition.entrySet()) {
					Object[] values = entry.getValue();
					if (values.length == 2) {
						queryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).from(values[0]).to(values[1]).includeLower(true).includeUpper(true));
					}
				}
			}

			if (topLeft != null && bottomRight != null) {
				queryBuilder.must(QueryBuilders.geoBoundingBoxQuery("position").setCorners(topLeft, bottomRight));
			}

			AggregationBuilder aggregation =
					AggregationBuilders
							.geohashGrid("agg")
							.field("position")
							.precision(precision);

			searchRequestBuilder.addAggregation(aggregation);
			searchRequestBuilder.setQuery(QueryBuilders.boolQuery().filter(queryBuilder));
			logger.info("search heatmap sql = {}",searchRequestBuilder);
			SearchResponse response = searchRequestBuilder.execute().actionGet();
			GeoHashGrid agg = response.getAggregations().get("agg");
			// For each entry
			for (GeoHashGrid.Bucket entry : agg.getBuckets()) {
				GeoPoint key = (GeoPoint) entry.getKey();    // key as geo point
				long docCount = entry.getDocCount();         // Doc count
				Location l = new Location();
				l.setCount(docCount);
				l.setLat(key.getLat());
				l.setLon(key.getLon());
				list.add(l);
			}
		} catch (IndexNotFoundException e) {
			list = new ArrayList<Location>();
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.INDEX_NOT_FOUND_ERROR,
					"no such index.");
		} catch (Exception e) {
			list = new ArrayList<Location>();
			throw new HttpInternalServerError("internal server error");
		} finally {
			return list;
		}
	}

	public BatchcreateResult deleteVehicles(Map<String, Object> queryMap) throws HttpException {
		BatchcreateResult br = new BatchcreateResult();
		if (StringUtil.isEmpty(queryMap.get("vins")) && StringUtil.isEmpty(queryMap.get("vrns"))) {
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER_VINVRN,
					"vins or vrns must only one not null.");
		}
		if (StringUtil.isEmpty(queryMap.get("startTime")) && StringUtil.isEmpty(queryMap.get("endTime"))) {
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.START_OR_END_TIMENOTFOUND_ERROR,
					"startTime and endTime parameters required.");
		}
		validateVinVrnLimit(queryMap);
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		buildTermQueryWithoutType(queryMap, "oemId", queryBuilder, "oemId");
		buildTermsQueryWithType(queryMap,"vins", queryBuilder, "vin");
		buildTermsQueryWithType(queryMap,"vrns", queryBuilder, "vrn");
		buildTermsMatchPhraseQuery(queryBuilder, "fleetId",(List<String>)queryMap.get("fleetIds"));
		buildTermsMatchPhraseQuery(queryBuilder, "enterpriseId",(List<String>)queryMap.get("enterpriseIds"));
		buildTermsMatchPhraseQuery(queryBuilder, "enterpriseType",(List<String>)queryMap.get("enterpriseTypes"));
		buildTermsMatchPhraseQuery(queryBuilder, "vehicleType",(List<String>)queryMap.get("vehicleTypes"));
		buildTimeRandQuery(queryBuilder,"timestamp",(String) queryMap.get("startTime"),(String) queryMap.get("endTime"));
		buildGeofenceQuery(queryMap,queryBuilder);

		boolean isLimit = false;
		Set<String> searchIndices = getSearchIndexForSearchAfter(queryMap,false);
		List<String> idsList = searchIdsByIndexs(queryBuilder,searchIndices);

		StringBuilder strIndexValues = new StringBuilder();
		if (searchIndices != null) {
			String[] indices = searchIndices.toArray(new String[searchIndices.size()]);
			if (indices != null) {
				for (int i = 0; i < indices.length; i++) {
					strIndexValues.append(i == 0 ? "" : ",");
					if (indices[i] != null) {
						strIndexValues.append(indices[i].toString().trim());
					}
				}
			}
		}
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int i = 0;
			for (String id : idsList) {
				bulkRequest.add(client.prepareDelete(strIndexValues.toString(), GIS_DATA_TYPE, id).request());
				i ++ ;
			}
			br.setSuccessfulItems(String.valueOf(i));
			BulkResponse bulkResponse = bulkRequest.get();
			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse item : bulkResponse.getItems()) {
					logger.info("delete vehicles error:", item.getFailureMessage());
				}
			} else {
				logger.info("delete vehicles ok");
			}
		}catch (Exception e){}
		return br;
	}


	public VehiclePolygon searchVehicles(Map<String, Object> queryMap) throws HttpException {
		VehiclePolygon vr = new VehiclePolygon();
		if(StringUtil.isEmpty(queryMap.get("vins")) && StringUtil.isEmpty(queryMap.get("vrns")) && StringUtil.isEmpty(queryMap.get("geofence"))){
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER_GEOVINVRN,
					"vins or vrns or geofence must only one not null.");
		}
		if(StringUtil.isEmpty(queryMap.get("startTime")) && StringUtil.isEmpty(queryMap.get("endTime"))){
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.START_OR_END_TIMENOTFOUND_ERROR,
					"startTime and endTime parameters required.");
		}
		validateVinVrnLimit(queryMap);
		searchGeofenceValidation(queryMap);
		String response = "";
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		BoolQueryBuilder queryGroupBuilder = new BoolQueryBuilder();

		buildTermQueryWithoutType(queryMap, "oemId", queryBuilder, "oemId");
		buildTermsQueryWithType(queryMap,"vins", queryBuilder, "vin");
		buildTermsQueryWithType(queryMap,"vrns", queryBuilder, "vrn");

		buildTermsMatchPhraseQuery(queryBuilder, "fleetId",(List<String>)queryMap.get("fleetIds"));
		buildTermsMatchPhraseQuery(queryBuilder, "enterpriseId",(List<String>)queryMap.get("enterpriseIds"));
		buildTermsMatchPhraseQuery(queryBuilder, "enterpriseType",(List<String>)queryMap.get("enterpriseTypes"));
		buildTermsMatchPhraseQuery(queryBuilder, "vehicleType",(List<String>)queryMap.get("vehicleTypes"));
		buildTimeRandQuery(queryBuilder,"timestamp",(String) queryMap.get("startTime"),(String) queryMap.get("endTime"));
		buildGeofenceQuery(queryMap,queryBuilder);


		//针对分组后的查询语句
		buildTermQueryWithoutType(queryMap, "oemId", queryGroupBuilder, "oemId");
		buildTermsQueryWithType(queryMap,"vrns", queryGroupBuilder, "vrn");
		buildTermsExactQuery(queryGroupBuilder, "fleetId",(List<String>)queryMap.get("fleetIds"));
		buildTermsExactQuery(queryGroupBuilder, "enterpriseId",(List<String>)queryMap.get("enterpriseIds"));
		buildTermsExactQuery(queryGroupBuilder, "enterpriseType",(List<String>)queryMap.get("enterpriseTypes"));
		buildTermsExactQuery(queryGroupBuilder, "vehicleType",(List<String>)queryMap.get("vehicleTypes"));
		buildTimeRandQuery(queryGroupBuilder,"timestamp",(String) queryMap.get("startTime"),(String) queryMap.get("endTime"));
		buildGeofenceQuery(queryMap,queryGroupBuilder);

		// means that none of the query criteria match,
		if (!queryBuilder.hasClauses() && queryMap.get("pageSize") == null && queryMap.size() > 0) {
			return vr;
		}

		int pageNo = (Integer) queryMap.get("pageNo") == null ? 1 : (Integer) queryMap.get("pageNo");
		int pageSize = SEARCH_SIZE;
		if (queryMap.get("pageSize") != null) {
			try {
				pageSize = (Integer) queryMap.get("pageSize");
			} catch (ClassCastException e) {
				logger.warn(e.toString(), e);
				throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.PAGESIZE_FORMAT_ERROR,
						"pageSize is not a number.");
			}
		}
		boolean isLimit = true;
		Set<String> indices = getSearchIndexForSearchAfter(queryMap,true);
		String searchType = "";

		List<String> vinList =  searchVinGroup(queryBuilder,indices);
		if(!StringUtil.isEmpty(vinList)){
			buildTermsExactQuery(queryGroupBuilder,"vin",vinList);
			response = searchDocumentBySearchAfter(queryGroupBuilder,indices);
			searchType = (String) queryMap.get("searchType");
			searchType = searchType == null ? "SIMPLE" : searchType;
			vr = buildVehiclePolygonResponseForResult(response,pageNo,pageSize,searchType);
		}

		return vr;
	}


	private static void buildTermQueryWithoutType(Map<String, Object> queryMap, String queryField,
												  BoolQueryBuilder queryBuilder, String field) {
		if (queryMap.get(queryField) == null) {
			return;
		}
		String value = (String) queryMap.get(queryField);
		queryBuilder.filter(termQuery(field, value));
	}

	private static void buildTermsQueryWithType(Map<String, Object> queryMap, String queryField,
												BoolQueryBuilder queryBuilder, String field) {
		if (queryMap.get(queryField) == null) {
			return;
		}
		Map<String, Object> queryFieldMap = (Map<String, Object>) queryMap.get(queryField);
		String type = (String) queryFieldMap.get("type");
		List<String> values = (List<String>) queryFieldMap.get("values");
		if (StringUtil.isEmpty(type)) {
			type = EXACT;
		}

		if (type.equals(EXACT)) {
			buildTermsExactQuery(queryBuilder, field, values);
		} else if (type.equals("PREFIX")) {
			buildTermsPrefixQuery(queryBuilder, field, values);
		} else if (type.equals("WILDCARD")) {
			buildTermsWildcardQuery(queryBuilder, field, values);
		}
	}

	private static void buildTimeRandQuery(BoolQueryBuilder queryBuilder, String field, String startTime,String endTime) {
		queryBuilder.must(QueryBuilders.rangeQuery(field).gte(startTime).lte(endTime));
	}

	private static void buildTermsExactQuery(BoolQueryBuilder queryBuilder, String field, List<String> values) {
		if (StringUtil.isEmpty(values)) {
			return;
		}
//		queryBuilder.filter(termsQuery(field, values));

		QueryBuilder queryBuilderTemp = QueryBuilders.boolQuery();
		for (String obj:values) {
			((BoolQueryBuilder) queryBuilderTemp).should(QueryBuilders.matchPhraseQuery(field, obj));
		}
		queryBuilder.must(queryBuilderTemp);

	}

	private static void buildTermsMatchPhraseQuery(BoolQueryBuilder queryBuilder, String field, List<String> values) {
		if (StringUtil.isEmpty(values)) {
			return;
		}
		QueryBuilder queryBuilderTemp = QueryBuilders.boolQuery();
		for (String obj:values) {
			((BoolQueryBuilder) queryBuilderTemp).should(QueryBuilders.matchPhraseQuery(field, obj).slop(0));
		}
		queryBuilder.must(queryBuilderTemp);
	}


	private static void buildTermsPrefixQuery(BoolQueryBuilder queryBuilder, String field, List<String> values) {
		BoolQueryBuilder subQueryBuilder = new BoolQueryBuilder();
		if (!StringUtil.isEmpty(values)) {
			for (String value : values) {
				subQueryBuilder.should(prefixQuery(field, value));
			}
			subQueryBuilder.minimumNumberShouldMatch(1);
			queryBuilder.filter(subQueryBuilder);
		}
	}

	private static void buildTermsWildcardQuery(BoolQueryBuilder queryBuilder, String field, List<String> values) {
		BoolQueryBuilder subQueryBuilder = new BoolQueryBuilder();
		if (!StringUtil.isEmpty(values)) {
			for (String value : values) {
				subQueryBuilder.should(wildcardQuery(field, ("*" + value + "*")));
			}
			if (!values.isEmpty()) {
				subQueryBuilder.minimumNumberShouldMatch(1);
				queryBuilder.filter(subQueryBuilder);
			}
		}
	}

	private static void buildMatchExactQuery(BoolQueryBuilder queryBuilder, String field, List<String> values) {
		BoolQueryBuilder subQueryBuilder = new BoolQueryBuilder();
		if (!StringUtil.isEmpty(values)) {
			for (String value : values) {
				subQueryBuilder.should(matchQuery(field, value));
			}
			subQueryBuilder.minimumNumberShouldMatch(1);
			queryBuilder.filter(subQueryBuilder);
		}
	}

	private List<String> searchVinGroup(BoolQueryBuilder queryBuilder, Set<String> searchIndices) throws HttpInternalServerError {
		List<String> list = new ArrayList<String>();

		if ((queryBuilder != null) && (searchIndices != null)) {
			StringBuilder strIndexValues = new StringBuilder();
			String[] indices = searchIndices.toArray(new String[searchIndices.size()]);
			if (indices != null) {
				for (int i = 0; i < indices.length; i++) {
					strIndexValues.append(i == 0 ? "" : ",");
					if (indices[i] != null) {
						strIndexValues.append(indices[i].toString().trim());
					}
				}
			}
			SearchRequestBuilder requestBuilder = client.prepareSearch(strIndexValues.toString());
			if (queryBuilder.hasClauses()) {
				requestBuilder.setQuery(queryBuilder);
			}

			TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("vin_group").field("vin").order(Terms.Order.term(true));
			requestBuilder.addAggregation(termsBuilder);
			requestBuilder.setSize(MAX_SIZE).setScroll(TimeValue.timeValueMinutes(8));//这个游标维持多长时间
			requestBuilder.addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC).missing("_last")).addSort("_uid", SortOrder.ASC);
			logger.info("search group by search_after = "+requestBuilder);
			for (String nodes : clusterNodes.split(",")) {
				try {
					String InetSocket[] = nodes.split(":");
					String address = InetSocket[0];
					String url = ES_HTTP + address + ":" + ES_PORT;
					String resultJosn = HttlUtil.postRequest(url,strIndexValues.toString(),GIS_DATA_TYPE,requestBuilder.toString());
					list = bulidResponseGroupForJson(resultJosn);
					if(!StringUtil.isEmpty(list)){
						logger.info("search group by list size = "+list.size());
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	private String searchDocumentBySearchAfter(BoolQueryBuilder queryBuilder, Set<String> searchIndices) throws HttpInternalServerError {
		String strResponse = "";

		if ((queryBuilder != null) && (searchIndices != null)) {
			StringBuilder strIndexValues = new StringBuilder();
			String[] indices = searchIndices.toArray(new String[searchIndices.size()]);
			if (indices != null) {
				for (int i = 0; i < indices.length; i++) {
					strIndexValues.append(i == 0 ? "" : ",");
					if (indices[i] != null) {
						strIndexValues.append(indices[i].toString().trim());
					}
				}
			}
			SearchRequestBuilder requestBuilder = client.prepareSearch(strIndexValues.toString());
			if (queryBuilder.hasClauses()) {
				requestBuilder.setQuery(queryBuilder);
			}

			requestBuilder.setSize(MAX_SIZE).setScroll(TimeValue.timeValueMinutes(8));//这个游标维持多长时间
			requestBuilder.addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC).missing("_last")).addSort("_uid", SortOrder.ASC);
//			logger.info("search document by search_after = "+requestBuilder);
			for (String nodes : clusterNodes.split(",")) {
				try {
					String InetSocket[] = nodes.split(":");
					String address = InetSocket[0];
					String url = ES_HTTP + address + ":" + ES_PORT;
					String resultJosn = HttlUtil.postRequest(url,strIndexValues.toString(),GIS_DATA_TYPE,requestBuilder.toString());
//					logger.info("search document resultJosn = "+resultJosn);
					strResponse = bulidResponseForJson(resultJosn);
					if(!StringUtil.isEmpty(strResponse)){
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return strResponse;
	}


	private List<String> searchIdsByIndexs(BoolQueryBuilder queryBuilder, Set<String> searchIndices) throws HttpInternalServerError {
		List<String> idsList = new ArrayList<String>();
		String resultJosn = "";
		if ((queryBuilder != null) && (searchIndices != null)) {
			StringBuilder strIndexValues = new StringBuilder();
			String[] indices = searchIndices.toArray(new String[searchIndices.size()]);
			if (indices != null) {
				for (int i = 0; i < indices.length; i++) {
					strIndexValues.append(i == 0 ? "" : ",");
					if (indices[i] != null) {
						strIndexValues.append(indices[i].toString().trim());
					}
				}
			}
			SearchRequestBuilder requestBuilder = client.prepareSearch(strIndexValues.toString());
			if (queryBuilder.hasClauses()) {
				requestBuilder.setQuery(queryBuilder);
			}
			requestBuilder.setSize(MAX_SIZE).setScroll(TimeValue.timeValueMinutes(8));//这个游标维持多长时间
			requestBuilder.addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC).missing("_last")).addSort("_uid", SortOrder.ASC);
			logger.info("searchIdsByIndexs = "+requestBuilder);
			for (String nodes : clusterNodes.split(",")) {
				try {
					String InetSocket[] = nodes.split(":");
					String address = InetSocket[0];
					String url = ES_HTTP + address + ":" + ES_PORT;
					resultJosn = HttlUtil.postRequest(url,strIndexValues.toString(),GIS_DATA_TYPE,requestBuilder.toString());
					if(!StringUtil.isEmpty(resultJosn)){
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			JSONObject topJson = JSON.parseObject(resultJosn);
			JSONObject basicJson = ((JSONObject)topJson.getJSONObject("hits"));
			if(!StringUtil.isEmpty(basicJson)){
				JSONArray inputArray = ((JSONArray)basicJson.getJSONArray("hits"));
				BigInteger total = ((BigInteger)basicJson.getBigInteger("total"));
				if(!StringUtil.isEmpty(inputArray)) {
					for (int i = 0; i < total.intValue(); i++) {
						JSONObject termJson = inputArray.getJSONObject(i);
						String id = termJson.getString("_id");
						idsList.add(id);
					}
				}
			}
		}
		return idsList;
	}

	private VehiclePolygon buildVehiclePolygonResponseForResult(String json,int pageNo,int pageSize,String searchType) {
		VehiclesResponse vr = JsonObject.toBean(json,VehiclesResponse.class);
		VehiclePolygon vpolygon = new VehiclePolygon();
		List<VehiclePosition> vehicles = new ArrayList<VehiclePosition>();


		Map<String, List<EsBean>> map = new TreeMap<String, List<EsBean>>();
		if (!StringUtil.isEmpty(vr) && !StringUtil.isEmpty(vr.getVehicles())) {
			for (EsBean bean : vr.getVehicles()) {
				if (map.containsKey(bean.getVin())) {
					List<EsBean> t = map.get(bean.getVin());
					t.add(new EsBean(bean.getOemId(), bean.getVin(), bean.getEnterpriseId(), bean.getEnterpriseName(), bean.getFleetId(), bean.getVehicleType(), bean.getTimestamp(), bean.getPosition(), bean.getDriverName(), bean.getDriverLicenseNum(), bean.getId()));
					map.put(bean.getVin(), t);
				} else {
					List<EsBean> t = new ArrayList<EsBean>();
					t.add(new EsBean(bean.getOemId(), bean.getVin(), bean.getEnterpriseId(), bean.getEnterpriseName(), bean.getFleetId(), bean.getVehicleType(), bean.getTimestamp(), bean.getPosition(), bean.getDriverName(), bean.getDriverLicenseNum(), bean.getId()));
					map.put(bean.getVin(), t);
				}
			}
			for (Map.Entry<String, List<EsBean>> entry : map.entrySet()) {
				VehiclePosition vp = new VehiclePosition();
				List<VehiclePostionTime> fullPositionList =  new ArrayList<VehiclePostionTime>();
				if (entry.getValue().size() == 1) { //First last same position
					EsBean ebb = entry.getValue().get(0);

					vp.setDriverLicenseNum(ebb.getDriverLicenseNum());
					vp.setDriverName(ebb.getDriverName());
					vp.setVin(ebb.getVin());

					VehiclePostionTime vptFirst = new VehiclePostionTime();
					vptFirst.setLocation(ebb.getPosition());
					vptFirst.setTime(ebb.getTimestamp());
					vp.setFirst(vptFirst);

					VehiclePostionTime vpt = new VehiclePostionTime();
					vpt.setLocation(ebb.getPosition());
					vpt.setTime(ebb.getTimestamp());
					vp.setLast(vpt);
				}  else if (entry.getValue().size() >= 2) {
					TreeMap<String, EsBean> treeMap = new TreeMap<String, EsBean>();
					for (EsBean esb : entry.getValue()) {
						treeMap.put(esb.getTimestamp(), esb);
					}
					List<EsBean> overList = new ArrayList<EsBean>();
					while (treeMap.size() != 0) {
						overList.add(treeMap.firstEntry().getValue());
						treeMap.remove(treeMap.firstKey());
					}
					if (overList.size() == 1){
						EsBean ebb = overList.get(0);
						vp.setDriverLicenseNum(ebb.getDriverLicenseNum());
						vp.setDriverName(ebb.getDriverName());
						vp.setVin(ebb.getVin());
						VehiclePostionTime vptFirst = new VehiclePostionTime();
						vptFirst.setLocation(ebb.getPosition());
						vptFirst.setTime(ebb.getTimestamp());
						vp.setFirst(vptFirst);
						VehiclePostionTime vpt = new VehiclePostionTime();
						vpt.setLocation(ebb.getPosition());
						vpt.setTime(ebb.getTimestamp());
						vp.setLast(vpt);
					} else if (overList.size() >= 2){
						EsBean ebbFirst = overList.get(0);
						vp.setDriverLicenseNum(ebbFirst.getDriverLicenseNum());
						vp.setDriverName(ebbFirst.getDriverName());
						vp.setVin(ebbFirst.getVin());
						VehiclePostionTime vptFirst = new VehiclePostionTime();
						vptFirst.setLocation(ebbFirst.getPosition());
						vptFirst.setTime(ebbFirst.getTimestamp());
						vp.setFirst(vptFirst);
						EsBean ebbEnd = overList.get(overList.size()-1);
						VehiclePostionTime vptEnd = new VehiclePostionTime();
						vptEnd.setLocation(ebbEnd.getPosition());
						vptEnd.setTime(ebbEnd.getTimestamp());
						vp.setLast(vptEnd);
					}
				}

				if(searchType.equals("FULL")){
					for(EsBean esb: entry.getValue()){
						VehiclePostionTime vpt = new VehiclePostionTime();
						vpt.setTime(esb.getTimestamp());
						vpt.setLocation(esb.getPosition());
						fullPositionList.add(vpt);
					}
					vp.setPosition(fullPositionList);
				}
				vehicles.add(vp);
			}
			map.clear();
		}
		//刚开始的页面
		vpolygon.setPageNo(pageNo);
		//设置每页数据为十条
		vpolygon.setPageSize(pageSize);
		//每页的开始数
		vpolygon.setStar((vpolygon.getPageNo() - 1) * vpolygon.getPageSize());
		//list的大小
		int count = vehicles.size();
		vpolygon.setTotal(count);
		//设置总页数
		vpolygon.setTotalPage(count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
		//对list进行截取
		int endIndex = count-vpolygon.getStar() > vpolygon.getPageSize() ? vpolygon.getStar()+vpolygon.getPageSize(): count;
		logger.info("subList start = {},end = {}",vpolygon.getStar(),endIndex);
		try{
			vpolygon.setVehicles(vehicles.subList(vpolygon.getStar(),endIndex));
		}catch (Exception e){
			vpolygon.setVehicles(null);
		}
		return vpolygon;
	}

	private static void buildGeofenceQuery(Map<String, Object> queryMap, BoolQueryBuilder search)
			throws HttpClientException {
		if (queryMap.get("geofence") != null) {
			Map<String, Object> geofence = (Map<String, Object>) queryMap.get("geofence");
			String shape = (String) geofence.get("shape");
			if ("CircleGeofence".equals(shape)) {
				double radius = Double.parseDouble(geofence.get("radius").toString());
				Map<String, Object> center = (Map<String, Object>) geofence.get("center");
				double lat = Double.parseDouble(center.get("lat").toString());
				double lon = Double.parseDouble(center.get("lon").toString());
				search.filter(new GeoDistanceQueryBuilder("position").point(lat, lon).distance(radius,
						DistanceUnit.METERS));
			} else if ("PolygonGeofence".equals(shape)) {
				List<GeoPoint> points = new ArrayList<>();
				List<Map<String, Object>> vertices = (List<Map<String, Object>>) geofence.get("vertices");
				for (Map<String, Object> vertice : vertices) {
					double lat = Double.parseDouble(vertice.get("lat").toString());
					double lon = Double.parseDouble(vertice.get("lon").toString());
					points.add(new GeoPoint(lat, lon));
				}
				search.filter(new GeoPolygonQueryBuilder("position", points));
			}
		}
	}

	private String bulidResponseForJson(String json) throws Exception {
		String response = "";
		JSONArray outArray = new JSONArray();

		JSONObject topJson = JSON.parseObject(json);
		JSONObject basicJson = ((JSONObject)topJson.getJSONObject("hits"));
		if(!StringUtil.isEmpty(basicJson)){
			JSONArray inputArray = ((JSONArray)basicJson.getJSONArray("hits"));
			BigInteger total = ((BigInteger)basicJson.getBigInteger("total"));
			logger.info("search vehicles total = {}" ,total.intValue());
			if(!StringUtil.isEmpty(inputArray)) {
				for (int i = 0; i < total.intValue(); i++) {
					JSONObject termJson = inputArray.getJSONObject(i);
					JSONObject sourceJson = termJson.getJSONObject("_source");
					outArray.add(sourceJson);
				}
			}
		}
		response = "{\"vehicles\": " + outArray.toJSONString() + "}";
		return response;
	}

	private List<String> bulidResponseGroupForJson(String json) throws Exception {
//		logger.info("search group by resultJosn = "+json);
		List<String> list = new ArrayList<String>();
		JSONObject topJson = JSON.parseObject(json);
		JSONObject basicJson = ((JSONObject)topJson.getJSONObject("aggregations").getJSONObject("vin_group"));
		if(!StringUtil.isEmpty(basicJson)){
			JSONArray inputArray = ((JSONArray)basicJson.getJSONArray("buckets"));
			if(!StringUtil.isEmpty(inputArray)) {
				for (int i = 0; i < inputArray.size(); i++) {
					JSONObject termJson = inputArray.getJSONObject(i);
					String groupVinName = termJson.getString("key");
					list.add(groupVinName);
				}
			}
		}
		return list;
	}

	private StringBuilder appendSortValues(SearchHit hit) {
		StringBuilder strSortValues = new StringBuilder();
		Object[] arrSortValues = hit.getSortValues();
		if (arrSortValues != null) {
			for (int i = 0; i < arrSortValues.length; i++) {
				strSortValues.append(i == 0 ? "" : ",");
				if (arrSortValues[i] != null) {
					strSortValues.append(arrSortValues[i].toString().trim());
				}
			}
		}
		return strSortValues;
	}

	private void searchGeofenceValidation(final Map<String, Object> queryMap) throws HttpGeofenceInvalidException {
		if (queryMap == null) {
			return;
		}
		Map<String, Object> geofence = (Map<String, Object>) queryMap.get("geofence");
		validateGeofence(geofence);
	}

	private void validateVinVrnLimit(final Map<String, Object> queryMap) throws HttpClientException {
		if (queryMap == null) {
			return;
		}
		Map<String, Object> vinsMap = (Map<String, Object>) queryMap.get("vins");
		if (!StringUtil.isEmpty(vinsMap)) {
			List<String> vinsValues = (List<String>) vinsMap.get("values");
			if (!StringUtil.isEmpty(vinsValues)) {
				if (vinsValues.size() > searchLimit) {
					throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.SEARCH_LIMITE_ERROR, "The vins parameters can only be found within " + searchLimit + " records");
				}
			}
		}
		Map<String, Object> vrnsMap = (Map<String, Object>) queryMap.get("vrns");
		if (!StringUtil.isEmpty(vrnsMap)) {
			List<String> vrnsValues = (List<String>) vrnsMap.get("values");
			if (!StringUtil.isEmpty(vrnsValues)) {
				if (vrnsValues.size() > searchLimit) {
					throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.SEARCH_LIMITE_ERROR, "The vrns parameters can only be found within " + searchLimit + " records");
				}
			}
		}

	}

	private void validateGeofence(final Map<String, Object> geofence) throws HttpGeofenceInvalidException {
		if (geofence == null) {
			return;
		}
		String shape = (String) geofence.get("shape");
		if (!("CircleGeofence".equals(shape) || "PolygonGeofence".equals(shape))) {
			logger.warn("geofence format is invalid, shape: {}", shape);
			throw new HttpGeofenceInvalidException("geofence format is invalid");
		}

		if ("CircleGeofence".equals(shape)) {
			try {
				Map<String, Object> center = (Map<String, Object>) geofence.get("center");
				Double.parseDouble(center.get("lat").toString());
				Double.parseDouble(center.get("lon").toString());
				Double.parseDouble(geofence.get("radius").toString());
			} catch (Exception e) {
				logger.warn("geofence format is invalid: " + e.toString(), e);
				throw new HttpGeofenceInvalidException("geofence format is invalid");
			}
		} else if ("PolygonGeofence".equals(shape)) {
			try {
				List<Map<String, Object>> vertices = (List<Map<String, Object>>) geofence.get("vertices");
				if(StringUtil.isEmpty(vertices)){
					throw new HttpGeofenceInvalidException("geofence  vertices parameters required.");
				}
				if (vertices.size() < 3) {
					logger.warn("geofence format is invalid, vertices size: {} ", vertices.size());
					throw new HttpGeofenceInvalidException("geofence format is invalid");
				}
				for (Map<String, Object> vertice : vertices) {
					Double.parseDouble(vertice.get("lat").toString());
					Double.parseDouble(vertice.get("lon").toString());
				}
			} catch (Exception e) {
				logger.warn(e.toString(), e);
				throw new HttpGeofenceInvalidException("geofence format is invalid");
			}
		}
	}

	private Set<String> getSearchIndexForSearchAfter(Map<String, Object> queryMap,boolean isLimit) throws HttpClientException {
		Set<String> searchIndices;
		try {
			searchIndices = getSearchIndices(queryMap,isLimit);
		} catch (ParseException e) {
			logger.warn("parse startTime endTime in query failed", e);
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.TIME_FORMAT_ERROR,
					"startTime endTime format is incorrect");

		} catch (QueryException e) {
			logger.warn("search gis get query index error: " + e.getMessage(), e);
			throw new HttpClientException(HttpStatus.BAD_REQUEST, HttpErrorCode.TIME_SEATCH_LIMITE_ERROR, e.getMessage());
		}
		return searchIndices;
	}

	private Set<String> getSearchIndices(Map<String, Object> queryMap,boolean isLimit) throws ParseException, QueryException {
		Date to;
		Date from;

		Set<String> searchIndices = new HashSet<>();

		if (!StringUtil.isEmpty(queryMap.get("startTime")) && !StringUtil.isEmpty(queryMap.get("endTime"))) {
			from = getOffsetTime((String) queryMap.get("startTime"), 0);
			to = getOffsetTime((String) queryMap.get("endTime"), 0);

			if (to.before(from)) {
				logger.warn("endTime: {} before startTime: {}", to, from);
				throw new QueryException("endTime before startTime");
			}

			if(isLimit){
				long startTimeLong = getLongTime((String) queryMap.get("startTime"));
				long endTimeLong = getLongTime((String) queryMap.get("endTime"));
				long between_Hours=(endTimeLong-startTimeLong)/(1000*3600);
				if(between_Hours >= Long.valueOf(searchTimeInterval)){
					logger.warn("Only query data within 3 hours of time interval");
					throw new QueryException("Only query data within 3 hours of time interval");
				}
			}

			searchIndices = getBetweenQueryIndex(from, to);
		}
		return searchIndices;
	}

	private Date getOffsetTime(String time, int offset) throws ParseException {
		calendar.setTime(timeStampFormat.parse(time));
		calendar.add(Calendar.MINUTE, offset);
		return calendar.getTime();
	}

	private long getLongTime(String time) throws ParseException {
		calendar.setTime(timeStampFormat.parse(time));
		return calendar.getTimeInMillis();
	}
	private Set<String> getBetweenQueryIndex(Date from, Date to) throws ParseException, QueryException {
		Set<String> indices = new HashSet<>();
		List<Date> list = IndexCalculationUtil.getBetweenDates(from,to);
		if(!StringUtil.isEmpty(list)){
			for(Date d:list){
				String index = IndexCalculationUtil.getIndexFromTimestamp(timeStampFormat.format(d));
//				logger.info("search index:"+index);
				if (isIndexExists(index)) {
					indices.add(index);
				}
			}
		}
		return indices;
	}


	/**
	 * 得到全部索引
	 * @return 返回List封装的全部索引的名字
	 */
	public List<String> getIndexs(){
		List<String> list = new ArrayList<String>();
		String [] indices = client.admin().indices()
				.prepareGetIndex()
				.setFeatures()
				.get()
				.getIndices();
		for(int i = 0; i < indices.length; i++){
			list.add(indices[i]);
		}
		return list;
	}

	/**
	 * 得到过滤的关键字，即xxxx.xx.xx(年月日字符串)，这里设置为90天
	 * @return 过滤关键字
	 */
	public String getUpdateDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 0);
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = String.valueOf(calendar.get(Calendar.DATE));
		if(month.length() == 1){
			month = "0" + month;
		}
		if(day.length() == 1){
			day = "0" + day;
		}
		String keyword = year + "-" + month + "-" + day;
		return keyword;
	}

	public void updateOldIndexMapping(List<String> list, String updateDate){
		int count = 0;
		String mappingJson = "{\"properties\":{\"vin\":{\"type\":\"text\",\"fielddata\":true}}}";
		for(Iterator<String> iter = list.iterator(); iter.hasNext(); ){
			String str = iter.next();
			//索引名称字段长度比14小的话就不作为删除对象了
			if(str.length() >= 14){
				String dateStr = str.substring(4 ,str.length());//取出索引名称字段的结尾字段
				//索引名称字段最后不是以日期结尾的话也不作为删除对象
				String patternStr = "^[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}$";
				if(dateStr.matches(patternStr)){
					if(dateStr.compareTo(updateDate) < 0){
						for (String nodes : clusterNodes.split(",")) {
							try {
								String InetSocket[] = nodes.split(":");
								String address = InetSocket[0];
								String url = ES_HTTP + address + ":" + ES_PORT;
								String resultJosn = HttlUtil.putIndexMapping(url,str,GIS_DATA_TYPE,mappingJson);
								if(!StringUtil.isEmpty(resultJosn)){
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						count++;
					}
				}
			}
		}
		logger.info("The total mapping updated index is: " + count);
	}

}
