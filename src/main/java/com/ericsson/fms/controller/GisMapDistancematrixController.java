package com.ericsson.fms.controller;

import com.ericsson.fms.entity.Distancematrix;
import com.ericsson.fms.entity.QueryDistancematrixBody;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisMapDistancematrixService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/gis")
public class GisMapDistancematrixController extends BaseController{
	@Autowired
	private GisMapDistancematrixService gisMapDistancematrixService;
	
	@RequestMapping(value = "/maps/v1/rpc/search-distancematrix", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public Distancematrix getGisMapDistancematrix(@RequestBody QueryDistancematrixBody queryDistancematrixBody) throws HttpException{
		logger.info("=======getGisMapDistancematrix=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Distancematrix distancematrix = new Distancematrix();
			distancematrix = gisMapDistancematrixService.getDistancematrix(queryDistancematrixBody);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisMapDistancematrix time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisMapDistancematrix result:"+mapper.writeValueAsString(distancematrix));
			return distancematrix;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
        	logger.info(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
	}


}
