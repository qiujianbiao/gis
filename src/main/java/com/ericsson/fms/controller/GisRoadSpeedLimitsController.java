package com.ericsson.fms.controller;

import com.ericsson.fms.entity.SpeedLimits;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisMapSpeedLimitsService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/gis")
public class GisRoadSpeedLimitsController extends BaseController{
	@Autowired
	private GisMapSpeedLimitsService gisMapSpeedLimitsService;
	
	@RequestMapping(value = "/road/v1/speed-limits", method = RequestMethod.GET)
	public SpeedLimits getGisRoadSpeedLimits(Double lat, Double lon) throws HttpException{
		logger.info("=======getGisRoadSpeedLimits=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			SpeedLimits speedLimits = new SpeedLimits();
			speedLimits = gisMapSpeedLimitsService.getLimitSpeed(lat,lon);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisRoadSpeedLimits time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisRoadSpeedLimits result:"+mapper.writeValueAsString(speedLimits));
			return speedLimits;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
        	logger.info(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
	}


}
