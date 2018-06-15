package com.ericsson.fms.controller;

import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.Location;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.ElasticSearchService;
import com.ericsson.fms.service.TripHeatmapService;
import com.ericsson.fms.utils.GzipUtils;
import com.ericsson.fms.utils.IndexCalculationUtil;
import com.ericsson.fms.utils.JsonObject;
import com.ericsson.fms.utils.StringUtil;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value="/gis")
public class GisHeatMapController extends BaseController{
	@Autowired
	private ElasticSearchService elasticSearchService;
	@Autowired
	private TripHeatmapService tripHeatmapService;
	
	@Value("${timestamp.format}")
    private String timeStampFormatString;
	private SimpleDateFormat indexFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private String getHeatmap(@RequestBody Map<String, Object> gisHeatMap) throws HttpException{
		long starttime = System.currentTimeMillis();
		logger.info("=======getGisHeatmap=======");
		try {
			boolean isRequest = false;
			ArrayList vehicleTypes = (ArrayList)gisHeatMap.get("vehicleTypes");
			ArrayList enterpriseIds = (ArrayList)gisHeatMap.get("enterpriseIds");
			ArrayList enterpriseTypes = (ArrayList)gisHeatMap.get("enterpriseTypes");
			ArrayList fleetIds = (ArrayList)gisHeatMap.get("fleetIds");


			String[] enterpriseIdArrays = null;
			String[] enterpriseTypeArrays = null;
			String[] vehicleTypeArrays = null;
			String[] fleetIdArrays = null;

			String oemId = (String)gisHeatMap.get("oemId");
			String type = (String)gisHeatMap.get("type");
			Integer precision = (Integer)gisHeatMap.get("precision");
			precision = (precision == null) ? 7 : precision;
			String topLefts = (String)gisHeatMap.get("topLeft");
			String bottomRights = (String)gisHeatMap.get("bottomRight");
			String startTime = (String)gisHeatMap.get("startTime");
			String endTime = (String)gisHeatMap.get("endTime");
			GeoPoint topLeft = null;
			GeoPoint bottomRight = null;

			if(StringUtil.isEmpty(type)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(startTime)){
				isRequest = true;
			}
			if(StringUtil.isEmpty(endTime)){
				isRequest = true;
			}
			if(isRequest){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}

			logger.info("enterpriseIds[{}],type[{}],enterpriseTypes[{}],vehicleTypes[{}],oemId[{}],fleetIds[{}]",enterpriseIds,type,enterpriseTypes,vehicleTypes,oemId,fleetIds);
			if(StringUtil.isEmpty(startTime)){
				throw new HttpException(HttpStatus.NOT_FOUND,HttpErrorCode.START_OR_END_TIMENOTFOUND_ERROR,"The parameter startTime is missing.");
			}
			if(StringUtil.isEmpty(endTime)){
				throw new HttpException(HttpStatus.NOT_FOUND,HttpErrorCode.START_OR_END_TIMENOTFOUND_ERROR,"The parameter endTime is missing.");
			}

			if(!StringUtil.isEmpty(vehicleTypes)){
				vehicleTypeArrays = new String[vehicleTypes.size()];
				vehicleTypes.toArray(vehicleTypeArrays);
			}
			if(!StringUtil.isEmpty(enterpriseTypes)){
				enterpriseTypeArrays = new String[enterpriseTypes.size()];
				enterpriseTypes.toArray(enterpriseTypeArrays);
			}
			if(!StringUtil.isEmpty(enterpriseIds)){
				enterpriseIdArrays = new String[enterpriseIds.size()];
				enterpriseIds.toArray(enterpriseIdArrays);
			}
			if(!StringUtil.isEmpty(fleetIds)){
				fleetIdArrays = new String[fleetIds.size()];
				fleetIds.toArray(fleetIdArrays);
			}

			Date to;
	        Date from;
	        to = getOffsetTime(endTime, 0);
            from = getOffsetTime(startTime, 0);
            if (to.before(from)) {
                throw new HttpException(HttpStatus.BAD_REQUEST,HttpErrorCode.ENDTIME_BEFORE_STARTTIME_ERROR,"endTime.to before endTime.from.");
            }
            
			List<Location> list = new ArrayList<Location>();
			Map<String, Object[]> conditionMap = new HashMap<String, Object[]>();
			Map<String, Object[]> rangeMap = new HashMap<String, Object[]>();
			Map<String, Object[]> inConditionMap = new HashMap<String, Object[]>();

			if(!StringUtil.isEmpty(oemId)){
				Object[] objoemIds = new Object[1];
				objoemIds[0] = oemId;
				conditionMap.put("oemId", objoemIds);
			}
			if(!StringUtil.isEmpty(vehicleTypes)){
				Object[] objVehicleTypes = new Object[vehicleTypes.size()];
				inConditionMap.put("vehicleType", vehicleTypes.toArray(objVehicleTypes));
			}
			if(!StringUtil.isEmpty(enterpriseIds)){
				Object[] objEnterpriseIds = new Object[enterpriseIds.size()];
				inConditionMap.put("enterpriseId", enterpriseIds.toArray(objEnterpriseIds));
			}
			if(!StringUtil.isEmpty(enterpriseTypes)){
				Object[] objEnterpriseTypes = new Object[enterpriseTypes.size()];
				inConditionMap.put("enterpriseType", enterpriseTypes.toArray(objEnterpriseTypes));
			}
			if(!StringUtil.isEmpty(fleetIds)){
				Object[] objfleetIds = new Object[fleetIds.size()];
				inConditionMap.put("fleetId", fleetIds.toArray(objfleetIds));
			}
			
			if(!StringUtil.isEmpty(topLefts) && !StringUtil.isEmpty(bottomRights)){
				Object[] topLeftObject = topLefts.split(",");
				topLeft = new GeoPoint(Double.valueOf((String)topLeftObject[0]), Double.valueOf((String)topLeftObject[1]));
				Object[] bottomRightObject = bottomRights.split(",");
				bottomRight = new GeoPoint(Double.valueOf((String)bottomRightObject[0]), Double.valueOf((String)bottomRightObject[1]));
			}

			String resultStr = "";
			if(!StringUtil.isEmpty(type)){
				if(type.equals("vehicle-location")){
					String index = "";
					String indexBegin = IndexCalculationUtil.getIndexFromTimestamp(startTime);
					String indexEnd = IndexCalculationUtil.getIndexFromTimestamp(endTime);

					if(indexBegin.equals(indexEnd)){
						index = indexBegin;
						Object[] objTime = new Object[2];
						objTime[0] = startTime;
						objTime[1] = endTime;
						rangeMap.put("timestamp", objTime);
						list = elasticSearchService.search(index, precision, topLeft, bottomRight, conditionMap, null, null, rangeMap, inConditionMap);
					} else {
		                List<String> indexList = new ArrayList<String>();
		                indexList = findDates(startTime,endTime);
		                for (String indexNo : indexList){
		                	Object[] objTime = new Object[2];
		                	objTime[0] = indexNo.substring(4) + startTime.substring(10);
							objTime[1] = indexNo.substring(4) +  endTime.substring(10);
							rangeMap.put("timestamp", objTime);
		                	try{
			                	List<Location> listIndex = elasticSearchService.search(indexNo, precision, topLeft, bottomRight, conditionMap, null, null, rangeMap, inConditionMap);
			                	if(listIndex!=null){
			                		list.addAll(listIndex);
			                	}
							}catch(IndexNotFoundException ine){
								ine.printStackTrace();
		                		continue;
		                	}
		                }
					}
//					list = elasticSearchService.searchHeatmap("timestamp", startTime, endTime, oemId, "position",topLeft, bottomRight, enterpriseIdArrays, enterpriseTypeArrays,vehicleTypeArrays, fleetIdArrays,precision);
					resultStr = JsonObject.toJSONString(list);
				} else if(type.equals("trip-pickup") || type.equals("trip-dropoff")){
					resultStr = tripHeatmapService.getTipHeatmap(gisHeatMap);
				} else {
					throw new HttpException(HttpStatus.NOT_FOUND,HttpErrorCode.TYPE_FORMAT_ERROR,"The parameter type value is error.");
				}
			} else {
				throw new HttpException(HttpStatus.NOT_FOUND,HttpErrorCode.TYPE_NOT_FOUND_ERROR,"The parameter type is missing.");
			}
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisHeatmap time:"+(endtime-starttime)+"ms");
			if(StringUtil.isEmpty(resultStr)){
				String[] zero = new String[0];
				resultStr = JsonObject.toJSONString(zero);
			}
			logger.info("type:{},resultStr{}",type,resultStr);
			return resultStr;
		} catch (HttpException e) {
			e.printStackTrace();
            throw e;
        } catch (IndexNotFoundException e) {
			e.printStackTrace();
            throw new HttpInternalServerError("no such index.");
        } catch (Exception e) {
			e.printStackTrace();
            throw new HttpInternalServerError("Internal server error");
        }
	}

	/**
	 * modify interface name
	 * @param gisHeatMap
	 * @return
	 * @throws HttpException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/heatmap/v1/heatmap-grids", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
    public Object getGisHeatmap(@RequestBody Map<String, Object> gisHeatMap, @RequestHeader ("Accept-Encoding") String acceptEnc) throws HttpException {
		boolean isGzip = false;
		if (!StringUtil.isEmpty(acceptEnc)) {
			if (acceptEnc.compareToIgnoreCase("gzip") >= 0) {
				isGzip = true;
			}
		}
		Object returnObj = null;
		logger.info("Accept Encoding : " + acceptEnc + ";isGzip : " + isGzip);
		String resultStr = "";
		try{
			resultStr = getHeatmap(gisHeatMap);
			if (isGzip) {
				byte[] rsBytes = GzipUtils.compressToByte(resultStr);
				returnObj = rsBytes;
			} else {
				returnObj = resultStr;
			}
		} catch (HttpException e) {
			throw e;
		} catch (IndexNotFoundException e) {
			throw new HttpInternalServerError("no such index.");
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return returnObj;
	}
	
	private Date getOffsetTime(String time, int offset) throws ParseException {
        calendar.setTime(indexFormat.parse(time));
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> findDates(String dBegin, String dEnd) throws ParseException{  
  	  List lDate = new ArrayList();  
  	  Calendar calBegin = Calendar.getInstance();  
  	  calBegin.setTime(indexFormat.parse(dBegin));  
  	  Calendar calEnd = Calendar.getInstance();  
  	  calEnd.setTime(indexFormat.parse(dEnd));  
  	  lDate.add("gis-" + indexFormat.format(calBegin.getTime())); 
  	  while (calEnd.getTime().after(calBegin.getTime())){  
  	   calBegin.add(Calendar.DATE, 1);  
  	   lDate.add("gis-" + indexFormat.format(calBegin.getTime()));  
  	  }  
  	  return lDate;  
  	}
}
