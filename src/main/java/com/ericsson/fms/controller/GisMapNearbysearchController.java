package com.ericsson.fms.controller;

import com.ericsson.fms.entity.Nearbysearch;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisMapNearbysearchService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/gis")
public class GisMapNearbysearchController extends BaseController{
	@Autowired
	private GisMapNearbysearchService gisMapNearbysearchService;
	
	@RequestMapping(value = "/maps/v1/nearbysearch", method = RequestMethod.GET)
	public Nearbysearch getGisMapNearbysearch(Double lat, Double lon,Integer radius,String type,String language) throws HttpException{
		logger.info("=======getGisMapNearbysearch=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Nearbysearch nearbysearch = new Nearbysearch();
			nearbysearch = gisMapNearbysearchService.getNearbysearch(lat,lon,radius,type,language);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisMapNearbysearch time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisMapNearbysearch result:"+mapper.writeValueAsString(nearbysearch));
			return nearbysearch;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
        	logger.info(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
	}


}
