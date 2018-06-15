package com.ericsson.fms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.dao.GisRoadDao;
import com.ericsson.fms.domain.*;
import com.ericsson.fms.entity.*;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.utils.HttlUtil;
import com.ericsson.fms.utils.RedisComponent;
import com.ericsson.fms.utils.RedisKey;
import com.ericsson.fms.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class GisRoadService{
	public static final Logger logger = LoggerFactory.getLogger(GisRoadService.class);

	@Resource
	private GisRoadDao gisRoadDao;
	@Autowired
	private RedisComponent redisComponent;
	@Autowired
	GisMapSpeedLimitsService gisMapSpeedLimitsService;

	@Value("${gis.distance}")
    private Double distance;
	@Value("${gismap.city.loblock}")
	private Double cityloblock;
	@Value("${gismap.city.lablock}")
	private Double citylablock;
	@Value("${gismap.google.api_url}")
	private String api_url;
	@Value("${gismap.google.api_key}")
	private String api_key;
	@Value("${gismap.google.proxy_switch}")
	private String proxy_switch;
	@Value("${gismap.google.proxy_ip}")
	private String proxy_ip;
	@Value("${gismap.google.proxy_port}")
	private String proxy_port;

	@Value("${gis.uploadNum}")
	private Integer uploadNum;

	@Value("${gismap.active}")
	private String active = "";
	@Value("${gismap.redis.value.keepdays}")
	private long redisKeepDays = 90;

	public List<RoadBean> getRoadInfo(LocationBean lbean) throws HttpInternalServerError{
		try {
			lbean.setDistance(distance);
			logger.info("getRoadInfo param : lat[{}],lon[{}],Distance[{}]",lbean.getLat(),lbean.getLon(),lbean.getDistance());
			List<RoadBean> list = gisRoadDao.getRoadInfo(lbean);

            return list;
        } catch (Exception e) {
        	logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error.");
        }
	}
	
	public List<RoadBean> getPlaceInfo(LocationBean lbean) throws HttpInternalServerError{
		try {
			logger.info("getPlaceInfo param : lat[{}],lon[{}]",lbean.getLat(),lbean.getLon());
			List<RoadBean> list = gisRoadDao.getPlaceInfo(lbean);

            return list;
        } catch (Exception e) {
        	logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error.");
        }
	}
	
	/**
	 * 
	 * <p>Description: 根据经度，纬度获取该处所在城市
	 *                 <br/>先去redis中取，若有直接返回，若没有再调用google api去查询城市，并将查询回来的城市放入redis中<p>
	 * @param longitude_x 经度
	 * @param latitude_y 纬度
	 * @return cityname 所在城市（找不到或失败返回空字符串）
	 * @author huwei
	 * @date 2017年8月14日下午2:40:14
	 */
	public String getCityName(Double longitude_x,Double latitude_y,String language){
		String cityname = "";
		cityloblock = (cityloblock == null) ? 0.005 : cityloblock;
		citylablock = (citylablock == null) ? 0.005 : citylablock;
		logger.info("getCityName param : lat[{}],lon[{}],cityloblock[{}]",latitude_y,longitude_x,cityloblock);
		try {
			String redisKey = RedisKey.getRedisKey(longitude_x, latitude_y, cityloblock, citylablock);
			cityname = redisComponent.get(redisKey);
			if (!StringUtil.isEmpty(cityname)) {
				logger.info(String.format("CITY REDIS GET{longitude_X=%s,latitude_Y=%s,redisKey=%s,city=%s}", longitude_x, latitude_y, redisKey, cityname));
			} else {
				logger.info("REDIS GET CITYNAME EMPTY,TURN TO GOOGLE API");
				cityname = getCity(longitude_x, latitude_y, language);
				if (!StringUtil.isEmpty(cityname)) {
					logger.info("REDIS SET key=" + redisKey + " value=" + cityname + " keepdays=" + redisKeepDays);
					redisComponent.set(redisKey, cityname, redisKeepDays);
				}
			}
		} catch(Exception e){
			logger.warn("GisRoadService.getCityName fail",e);
		}
		return cityname;
	}
	
	private String getCity(Double longitude_x,Double latitude_y,String language) {
		String cityname = "";
		StringBuffer params = new StringBuffer();
		params.append("key=").append(api_key); //使用普通http工具的请求法
		params.append("&latlng=");
		params.append(latitude_y+","+longitude_x);
		if(!StringUtil.isEmpty(language)){
			params.append("&language=").append(language);
		} else {
			Locale locale = LocaleContextHolder.getLocale();
			if(!StringUtil.isEmpty(locale.getLanguage())){
				params.append("&language=").append(locale.getLanguage());
			} else {
				params.append("&language=en");
			}
		}
		logger.info("GoogleMapsApiUtil getCityInfo param:"+params.toString());

		String res;
		if(proxy_switch != null && "1".equals(proxy_switch)){
			res = HttlUtil.sendGet(api_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
		}else{
			res = HttlUtil.sendGet(api_url, params.toString(), false, null, null);
		}
		
		if (StringUtil.isEmpty(res)){
			return null;
		}
		logger.info("GoogleMapsApiUtil getCityInfo res="+res);
		
		JSONObject json = JSONObject.parseObject(res);
		if(json.containsKey("results")){
			JSONArray reArray = (JSONArray) json.get("results");
			for(int i=0; i<reArray.size(); i++ ){
				JSONObject rejson = reArray.getJSONObject(i);
				JSONArray addArray = (JSONArray)rejson.get("address_components");
				for(int k=0; k<addArray.size(); k++ ){
					JSONObject add = addArray.getJSONObject(k);
					JSONArray typeArray = (JSONArray)add.get("types");
					if(typeArray.size()==2){
						String type0 = typeArray.getString(0);
						String type1 = typeArray.getString(1);
						if(type0.equals("locality")&&type1.equals("political")){
							cityname = add.getString("long_name");
							break;
						}
					}
				}
				if(!StringUtil.isEmpty(cityname)){
					break;
				}
			}
		}
		logger.info("longitude_x="+longitude_x+" latitude_y="+latitude_y+" city_name="+cityname);
		
		return cityname;
	}

	public List<RoadHistory> getRoadHistorys(RoadHistoryRequest requset) throws HttpException{
		try {
			boolean isRequest = false;
			if(StringUtil.isEmpty(requset)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(requset.getCreateTime())){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			logger.info("getRoadHistorys param : createtime[{}],city[{}],country[{}],county[{}],state[{}]",requset.getCreateTime(),requset.getCity(),requset.getCountry(),requset.getCounty(),requset.getState());
			List<RoadHistory> list = gisRoadDao.getRoadHistorys(requset);
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			TimeZone utc = TimeZone.getTimeZone("UTC");
			f.setTimeZone(utc);
			GregorianCalendar cal = new GregorianCalendar(utc);
			for(RoadHistory r:list){
				cal.setTime(r.getCreateTime());
				r.setCreateTimeStr(f.format(cal.getTime()));
			}
			return list;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}

	public void insertRoadHistoryList(List<RoadSegment> roadSegmentList) throws HttpException {
		HashMap<String,RoadHistory> historyMap = new HashMap<String,RoadHistory>();
		try {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat rh = new SimpleDateFormat("yyyyMMddHH");
			TimeZone utc = TimeZone.getTimeZone("UTC");
			f.setTimeZone(utc);
			GregorianCalendar cal = new GregorianCalendar(utc);
			cal.setTime(new Date());
			String vn = rh.format(cal.getTime());
			for (RoadSegment r : roadSegmentList) {
				RoadHistory roadHistory = new RoadHistory();
				roadHistory.setVersionNum(vn);
				roadHistory.setState(StringUtil.isEmpty(r.getState()) ? "Dubai":r.getState());
				roadHistory.setCounty(StringUtil.isEmpty(r.getCounty()) ? "Dubai":r.getCounty());
				roadHistory.setCountry(StringUtil.isEmpty(r.getCountry()) ? "United Arab Emirates":r.getCountry());
				roadHistory.setCity(StringUtil.isEmpty(r.getCity()) ? "Dubai":r.getCity());
				historyMap.put(roadHistory.getState() + roadHistory.getCounty() + roadHistory.getCountry() + roadHistory.getCity() + roadHistory.getVersionNum(), roadHistory);
			}
			logger.info("insertRoadHistoryList size:[{}]", historyMap.size());
			List<RoadHistory> roadHistoryList = new ArrayList<RoadHistory>();
			Set<String> set = historyMap.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {//遍历map
				String key = it.next();
				RoadHistory value = historyMap.get(key);
				roadHistoryList.add(value);
			}
			if (!StringUtil.isEmpty(roadHistoryList)) {
				gisRoadDao.insertRoadHistoryList(roadHistoryList);
			}
		} catch (Exception e) {
			throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.CLIENT_ERROR,"Client request error.");
		}
	}

	public BatchcreateResult insertRoadSegmentsByBatch(List<RoadSegment> roadSegmentList) throws HttpException{
		BatchcreateResult br = new BatchcreateResult();
		try {
			if(StringUtil.isEmpty(roadSegmentList)){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			for (RoadSegment r : roadSegmentList) {
				r.setState(StringUtil.isEmpty(r.getState()) ? "Dubai":r.getState());
				r.setCounty(StringUtil.isEmpty(r.getCounty()) ? "Dubai":r.getCounty());
				r.setCountry(StringUtil.isEmpty(r.getCountry()) ? "United Arab Emirates":r.getCountry());
				r.setCity(StringUtil.isEmpty(r.getCity()) ? "Dubai":r.getCity());
			}
			logger.info("insert insertRoadSegmentsByBatch size:[{}]",roadSegmentList.size());
			gisRoadDao.deleteRoadSegmentsBySegId(roadSegmentList);
			gisRoadDao.insertRoadSegmentsByBatch(roadSegmentList);

			List<Salik> salikList = gisRoadDao.getSalik();
			for(Salik si:salikList){
				LocationBean lbean = new LocationBean();
				lbean.setLat(si.getLat());
				lbean.setLon(si.getLon());
				RoadSegment roadSegment = gisRoadDao.getGisRoadSingle(lbean);
				if(!StringUtil.isEmpty(roadSegment)){
					roadSegment.setRoadName(si.getRoadName());
					roadSegment.setSalikName(si.getSalikName());
					gisRoadDao.updateGisRoad(roadSegment);
				}
			}
			br.setSuccessfulItems(String.valueOf(roadSegmentList.size()));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.CLIENT_ERROR,"Client request error.");
		} finally {
			return br;
		}
	}


	public RoadSegmentResponse findPageRoadSegment(Double lat, Double lon, String roadName, String country, String state, String county, String city, String segId, Integer count, Integer startIndex) throws HttpInternalServerError{
		try {
			boolean isRequest = false;
			if(StringUtil.isEmpty(count)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(startIndex)){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			RoadSegmentRequest rq = new RoadSegmentRequest();
			RoadSegmentResponse roadSegmentResponse = new RoadSegmentResponse();
			rq.setCity(city);
			rq.setCountry(country);
			rq.setCounty(county);
			rq.setDistance(distance);
			if(!StringUtil.isEmpty(lat)){
				rq.setLat(lat);
			}
			if(!StringUtil.isEmpty(lon)){
				rq.setLon(lon);
			}
			if(!StringUtil.isEmpty(segId)){
				rq.setSegId(BigInteger.valueOf(Long.valueOf(segId)));
			}
			rq.setState(state);
			logger.info("findPageRoadSegment param : segId[{}],roadName[{}],city[{}],country[{}],county[{}],count[{}],startIndex[{}]",segId,roadName,city,country,county,count,startIndex);
			PageHelper.startPage(startIndex, count);
			Page<RoadSegment> page = gisRoadDao.findPageRoadSegment(rq);
			PageInfo<RoadSegment> pageInfo = new PageInfo<RoadSegment>(page);
			roadSegmentResponse.setRoads(pageInfo.getList());
			roadSegmentResponse.setItemsPerPage(count);
			roadSegmentResponse.setStartIndex(startIndex);
			roadSegmentResponse.setTotalResult(Integer.parseInt(String.valueOf(pageInfo.getTotal())));
			return roadSegmentResponse;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}

	public RoadSegmentResponse findPageAllRoadSegment(Integer count, Integer startIndex) throws HttpInternalServerError{
		try {
			boolean isRequest = false;
			if(StringUtil.isEmpty(count)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(startIndex)){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			RoadSegmentRequest rq = new RoadSegmentRequest();
			RoadSegmentResponse roadSegmentResponse = new RoadSegmentResponse();
			logger.info("findPageAllRoadSegment param : count[{}],startIndex[{}]",count,startIndex);
			PageHelper.startPage(startIndex, count);
			Page<RoadSegment> page = gisRoadDao.findPageAllRoadSegment();
			PageInfo<RoadSegment> pageInfo = new PageInfo<RoadSegment>(page);
			roadSegmentResponse.setRoads(pageInfo.getList());
			roadSegmentResponse.setItemsPerPage(count);
			roadSegmentResponse.setStartIndex(startIndex);
			roadSegmentResponse.setTotalResult(Integer.parseInt(String.valueOf(pageInfo.getTotal())));
			return roadSegmentResponse;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}

	public List<RoadTraffic> getRoadTraffic(RoadTrafficRequest request) throws HttpException{
		try {
			boolean isRequest = false;
			if(StringUtil.isEmpty(request)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(request.getLat())){
				isRequest = true;
			}
			if(StringUtil.isEmpty(request.getLon())){
				isRequest = true;
			}
			if(StringUtil.isEmpty(request.getFore())){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			request.setDistance(distance);
			logger.info("getRoadTraffic param : lat[{}],lon[{}],Distance[{}],Fore[{}]",request.getLat(),request.getLon(),request.getDistance(),request.getFore());
			List<RoadTraffic> list = gisRoadDao.getRoadTraffic(request);
			return list;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}


	public List<RoadTraffic> getRoadTrafficHistory(RoadTrafficRequest request) throws HttpException{
		try {
			boolean isRequest = false;
			if(StringUtil.isEmpty(request)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(request.getLat())){
				isRequest = true;
			}
			if(StringUtil.isEmpty(request.getLon())){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			request.setDistance(distance);
			logger.info("getRoadTrafficHistory param : lat[{}],lon[{}],Distance[{}]",request.getLat(),request.getLon(),request.getDistance());
			List<RoadTraffic> list = gisRoadDao.getRoadTrafficHistory(request);
			return list;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}

	public void removeDuplicateRoadHistory() throws HttpInternalServerError{
		try {
			gisRoadDao.removeDuplicateRoadHistory();
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error.");
		}
	}
}
