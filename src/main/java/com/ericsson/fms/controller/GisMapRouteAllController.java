package com.ericsson.fms.controller;

import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.RouteMessage;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisMapRouteAllService;
import com.ericsson.fms.utils.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/gis")
public class GisMapRouteAllController extends BaseController{
	@Autowired
	private GisMapRouteAllService gisMapRouteAllService;
	
	@RequestMapping(value = "/maps/v1/route", method = RequestMethod.GET)
	public RouteMessage getGisMapRoute(Double originLat, Double originLon,Double destinationLat, Double destinationLon, String mode, String units,String departureTime,String language) throws HttpException{
		logger.info("=======getGisMapRoute=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			RouteMessage routeMessage = new RouteMessage();
			routeMessage = gisMapRouteAllService.getRoute(originLat, originLon,destinationLat, destinationLon, mode, units,departureTime,language);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisMapRoute time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisMapRoute result:"+mapper.writeValueAsString(routeMessage));
			return routeMessage;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
        	logger.info(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
	}

	@RequestMapping(value = "/maps/v1/mroute", method = RequestMethod.GET)
	public List<RouteMessage> getGisMapMRoute(Double originLat, Double originLon,String destination, String mode, String units,String departureTime,String language) throws HttpException{
		logger.info("=======getGisMapMRoute=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<RouteMessage> list = new ArrayList<RouteMessage>();

			if (StringUtil.isEmpty(destination)){
				throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
			}
			String[] dess = destination.split(";");
			for(String d:dess){
				String[] dloca = d.split(",");
				Double destinationLat = Double.parseDouble(dloca[0]);
				Double destinationLon = Double.parseDouble(dloca[1]);
				RouteMessage routeMessage = new RouteMessage();
				routeMessage = gisMapRouteAllService.getRoute(originLat, originLon,destinationLat, destinationLon, mode, units,departureTime,language);
				list.add(routeMessage);
			}
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getGisMapMRoute time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getGisMapMRoute result:"+mapper.writeValueAsString(list));
			return list;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			logger.info(e.toString(), e);
			throw new HttpInternalServerError("Internal server error");
		}
	}
}
