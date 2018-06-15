package com.ericsson.fms.controller;

import com.ericsson.fms.entity.CityInfo;
import com.ericsson.fms.entity.GeoCodeInfo;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisMapPlaceAndGeocodeService;
import com.ericsson.fms.utils.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping(value="/gis")
public class GisMapPlaceAndGeoCodeController extends BaseController{
	@Autowired
	private GisMapPlaceAndGeocodeService gisMapPlaceAndGeocodeService;
	@Value("${map.source}")
	private String source;

	@RequestMapping(value = "/maps/v1/place", method = RequestMethod.GET)
	public CityInfo getGisMapCityInfo(Double lat,Double lon,String language) throws HttpException{
		logger.info("=======getGisMapCityInfo=======");
		Locale locale = LocaleContextHolder.getLocale();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			CityInfo cityInfo = new CityInfo();
			if(source.compareToIgnoreCase("google") == 0){
				cityInfo = gisMapPlaceAndGeocodeService.getCity(lat,lon,language);
			} else if(source.compareToIgnoreCase("openstreetmap") == 0){
				cityInfo = gisMapPlaceAndGeocodeService.getCityFromOSM(lat,lon,language);
				if(!StringUtil.isEmpty(cityInfo) && !StringUtil.isEmpty(cityInfo.getFormattedAddress())){
					long endtime = System.currentTimeMillis();
					logger.info("gis-service getGisMapCityInfo time:"+(endtime-starttime)+"ms");
				} else {
					cityInfo = gisMapPlaceAndGeocodeService.getCity(lat,lon,language);
				}
			}
			logger.info("gis-service getGisMapCityInfo result:"+mapper.writeValueAsString(cityInfo));
			return cityInfo;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
        	logger.info(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
	}

	@RequestMapping(value = "/maps/v1/geocoding", method = RequestMethod.GET)
	public GeoCodeInfo getGisMapGeoCode(String address,String language) throws HttpException{
		logger.info("=======getGisMapGeoCode=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			GeoCodeInfo geoCodeInfo = new GeoCodeInfo();
			if(source.compareToIgnoreCase("google") == 0){
				geoCodeInfo = gisMapPlaceAndGeocodeService.getGeoCodeInfo(address,language);
			} else if(source.compareToIgnoreCase("openstreetmap") == 0){
				geoCodeInfo = gisMapPlaceAndGeocodeService.getGeoCodeInfoFormOSM(address,language);
				if(!StringUtil.isEmpty(geoCodeInfo) && !StringUtil.isEmpty(geoCodeInfo.getFormattedAddress())){
					long endtime = System.currentTimeMillis();
					logger.info("gis-service getGisMapGeoCode time:"+(endtime-starttime)+"ms");
				} else {
					geoCodeInfo = gisMapPlaceAndGeocodeService.getGeoCodeInfo(address,language);
				}
			}
			logger.info("gis-service getGisMapGeoCode result:"+mapper.writeValueAsString(geoCodeInfo));
			return geoCodeInfo;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			logger.info(e.toString(), e);
			throw new HttpInternalServerError("Internal server error");
		}
	}
		
}
