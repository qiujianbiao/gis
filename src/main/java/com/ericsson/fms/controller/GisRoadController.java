package com.ericsson.fms.controller;

import com.ericsson.fms.domain.RoadBean;
import com.ericsson.fms.domain.RoadHistory;
import com.ericsson.fms.domain.RoadSegment;
import com.ericsson.fms.domain.RoadTraffic;
import com.ericsson.fms.entity.*;
import com.ericsson.fms.entity.vehicle.VehiclePolygon;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.ElasticSearchService;
import com.ericsson.fms.service.GisRoadService;
import com.ericsson.fms.utils.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/gis")
public class GisRoadController extends BaseController{
	@Autowired
	private GisRoadService gisRoadService;
	@Autowired
	private ElasticSearchService elasticSearchService;
	
	@RequestMapping(value = "/road/v1/roadinfo", method = RequestMethod.GET)
	public RoadBean getGisRoadInfo(Double lat,Double lon,String language) throws HttpException{
		logger.info("=======getGisRoadInfo=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RoadBean roadBean = new RoadBean();
			LocationBean lbean = new LocationBean();
			lbean.setLat(lat);
			lbean.setLon(lon);
			String city = null;
			
			List<RoadBean> list = gisRoadService.getRoadInfo(lbean);
			if(list!=null && list.size()!=0){
				roadBean = list.get(0);
			}
			
			List<RoadBean> listPlace = gisRoadService.getPlaceInfo(lbean);
			if(listPlace!=null && listPlace.size()!=0){
				RoadBean roadBeanPlace = listPlace.get(0);
				roadBean.setLocationId(roadBeanPlace.getLocationId());
				roadBean.setLocationName(roadBeanPlace.getLocationName());
				roadBean.setPlaceName(roadBeanPlace.getPlaceName());
			}
			
			city = gisRoadService.getCityName(lon,lat,language);
			if(!StringUtil.isEmpty(city)) {
				roadBean.setCity(city);
			}
			
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisRoadInfo time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisRoadInfo result:"+mapper.writeValueAsString(roadBean));
			return roadBean;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpInternalServerError("Internal server error");
        }
	}


	@RequestMapping(value = "/road/v1/road-history", method = RequestMethod.GET)
	public List<RoadHistory> getRoadHistorys(String time, String country, String state, String county, String city) throws HttpException{
		List<RoadHistory> list = new ArrayList<RoadHistory>();
		logger.info("=======getRoadHistorys=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RoadHistoryRequest request = new RoadHistoryRequest();
			request.setCreateTime(time);
			request.setCountry(country);
			request.setState(state);
			request.setCounty(county);
			request.setCity(city);
			list = gisRoadService.getRoadHistorys(request);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisRoadInfo time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisRoadInfo result:"+mapper.writeValueAsString(list));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return list;
	}


	@RequestMapping(value = "/road/v1/road-segments", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public BatchcreateResult insertGisRoadByBatch(@RequestBody List<RoadSegment> roadSegmentList) throws HttpException{
		logger.info("=======insertGisRoadByBatch=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		BatchcreateResult br = new BatchcreateResult();
		try {
			//first delete,and insert batch
			br = gisRoadService.insertRoadSegmentsByBatch(roadSegmentList);
			gisRoadService.insertRoadHistoryList(roadSegmentList);
			gisRoadService.removeDuplicateRoadHistory();
			long endtime = System.currentTimeMillis();
			logger.info("gis-service insertGisRoadByBatch time:"+(endtime-starttime)+"ms");
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		} finally {
			return br;
		}
	}

	@RequestMapping(value = "/road/v1/road-segments", method = RequestMethod.GET)
	public RoadSegmentResponse findPageRoadSegment(Double lat, Double lon, String roadName, String country, String state, String county, String city, String segId, Integer count, Integer startIndex) throws HttpException {
		logger.info("=======findPageRoadSegment=======");
		RoadSegmentResponse roadSegmentResponse = new RoadSegmentResponse();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			roadSegmentResponse = gisRoadService.findPageRoadSegment(lat,lon,roadName,country,state,county,city,segId,count,startIndex);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service findPageRoadSegment time:"+(endtime-starttime)+"ms");
			logger.info("gis-service findPageRoadSegment result:"+mapper.writeValueAsString(roadSegmentResponse));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return roadSegmentResponse;
	}

	@RequestMapping(value = "/road/v1/road-segments-all", method = RequestMethod.GET)
	public RoadSegmentResponse findPageAllRoadSegment(Integer count, Integer startIndex) throws HttpException {
		logger.info("=======findPageAllRoadSegment=======");
		RoadSegmentResponse roadSegmentResponse = new RoadSegmentResponse();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			roadSegmentResponse = gisRoadService.findPageAllRoadSegment(count,startIndex);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service findPageAllRoadSegment time:"+(endtime-starttime)+"ms");
			logger.info("gis-service findPageAllRoadSegment result:"+mapper.writeValueAsString(roadSegmentResponse));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return roadSegmentResponse;
	}

	@RequestMapping(value = "/road/v1/road-traffic", method = RequestMethod.GET)
	public RoadTraffic getRoadTraffic(Double lat, Double lon,String startTime, String endTime,Integer fore) throws HttpException{
		logger.info("=======getRoadTraffic=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		RoadTraffic roadTraffic = new RoadTraffic();
		try {
			RoadTrafficRequest request = new RoadTrafficRequest();
			request.setLat(lat);
			request.setLon(lon);
			request.setStartTime(startTime);
			request.setEndTime(endTime);
			request.setFore(fore);
			List<RoadTraffic> list = gisRoadService.getRoadTraffic(request);
			if(list!=null && list.size()!=0){
				roadTraffic = list.get(0);
			}
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getRoadTraffic time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getRoadTraffic result:"+mapper.writeValueAsString(roadTraffic));
			return roadTraffic;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
	}

	@RequestMapping(value = "/road/v1/road-traffic-history", method = RequestMethod.GET)
	public RoadTraffic getRoadTrafficHistory(Double lat, Double lon, String startTime, String endTime) throws HttpException{
		logger.info("=======getRoadTrafficHistory=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		RoadTraffic roadTraffic = new RoadTraffic();
		try {
			RoadTrafficRequest request = new RoadTrafficRequest();
			request.setLat(lat);
			request.setLon(lon);
			request.setStartTime(startTime);
			request.setEndTime(endTime);
			List<RoadTraffic> list = gisRoadService.getRoadTrafficHistory(request);
			if(list!=null && list.size()!=0){
				roadTraffic = list.get(0);
			}
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getRoadTrafficHistory time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getRoadTrafficHistory result:"+mapper.writeValueAsString(roadTraffic));
			return roadTraffic;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
	}


	@RequestMapping(value = "/maps/v1/rpc/search-vehicles", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public VehiclePolygon searchVehicles(@RequestBody Map<String, Object> queryMap) throws HttpException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			logger.info("search-vehicles queryMap = " + mapper.writeValueAsString(queryMap));
			VehiclePolygon response = this.elasticSearchService.searchVehicles(queryMap);
			String result = mapper.writeValueAsString(response);
			logger.info("search-vehicles result = " + result);
			return response;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError();
		}
	}


	@RequestMapping(value = "/maps/v1/rpc/delete-vehicles", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public BatchcreateResult deleteVehicless(@RequestBody Map<String, Object> queryMap) throws HttpException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			logger.info("deleteVehicless queryMap = " + mapper.writeValueAsString(queryMap));
			BatchcreateResult br = this.elasticSearchService.deleteVehicles(queryMap);
			String result = mapper.writeValueAsString(br);
			logger.info("deleteVehicless result = " + result);
			return br;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError();
		}
	}

}
