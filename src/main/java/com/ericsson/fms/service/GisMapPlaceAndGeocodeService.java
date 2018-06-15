package com.ericsson.fms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.CityInfo;
import com.ericsson.fms.entity.GeoCodeInfo;
import com.ericsson.fms.entity.Position;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.utils.HttlUtil;
import com.ericsson.fms.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by ejioqiu on 11/9/2017.
 */
@Service
public class GisMapPlaceAndGeocodeService {
    public static final Logger logger = LoggerFactory.getLogger(GisMapPlaceAndGeocodeService.class);
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
    @Value("${map.source}")
    private String source;

    @Value("${gismap.openstreetmap.api_url}")
    private String openstreetmap_api_url;
    @Value("${gismap.openstreetmap.searchapi_url}")
    private String openstreetmap_searchapi_url;
    @Value("${gismap.openstreetmap.addressdetails}")
    private String addressDetails;
    @Value("${gismap.openstreetmap.zoom}")
    private String zoom;

    public CityInfo getCity(Double latitude_y,Double longitude_x,String language)  throws HttpInternalServerError {
        try {
            boolean isRequest = false;
            String cityname = "";
            CityInfo cityInfo = new CityInfo();
            StringBuffer params = new StringBuffer();
            params.append("key=").append(api_key); //使用普通http工具的请求法
            params.append("&latlng=");
            if(StringUtil.isEmpty(latitude_y)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(longitude_x)){
                isRequest = true;
            }
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
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            String res;
            if(proxy_switch != null && "1".equals(proxy_switch)){
                res = HttlUtil.sendGet(api_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
            }else{
                res = HttlUtil.sendGet(api_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("GoogleMapsApiUtil getCityInfo res="+res);
            Position position = new Position(latitude_y,longitude_x);
            cityInfo.setPosition(position);
            cityInfo.setSource(source);
            JSONObject json = JSONObject.parseObject(res);
            if(json.containsKey("results")){
                JSONArray reArray = (JSONArray) json.get("results");
                for(int i=0; i<reArray.size(); i++ ){
                    JSONObject rejson = reArray.getJSONObject(i);
                    JSONArray addArray = (JSONArray)rejson.get("address_components");
                    String formattedAddress = (String) rejson.get("formatted_address");
                    JSONArray typesArray = (JSONArray)rejson.get("types");
                    if(typesArray!=null && typesArray.size()!=0){
                        String[] types = new String[typesArray.size()];
                        for(int k=0; k<typesArray.size(); k++ ){
                            types[k] = (String) typesArray.getString(k);
                        }
                        cityInfo.setTypes(types);
                    }
                    cityInfo.setFormattedAddress(formattedAddress);
                    for(int k=0; k<addArray.size(); k++ ){
                        JSONObject add = addArray.getJSONObject(k);
                        JSONArray typeArray = (JSONArray)add.get("types");
                        if(typeArray.size()==2){
                            String type0 = typeArray.getString(0);
                            String type1 = typeArray.getString(1);
                            if(type0.equals("locality")&&type1.equals("political")){
                                cityname = add.getString("long_name");
                                cityInfo.setCityName(cityname);
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

            return cityInfo;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }

    public GeoCodeInfo getGeoCodeInfo(String address,String language) throws HttpInternalServerError {
        try {
            GeoCodeInfo geoCodeInfo = new GeoCodeInfo();
            boolean isRequest = false;
            StringBuffer params = new StringBuffer();
            params.append("key=").append(api_key); //使用普通http工具的请求法
            params.append("&address=");
            params.append(address);
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
            logger.info("GoogleMapsApiUtil getGeoCodeInfo param:"+params.toString());
            if(StringUtil.isEmpty(address)){
                isRequest = true;
            }
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            String res;
            if(proxy_switch != null && "1".equals(proxy_switch)){
                res = HttlUtil.sendGet(api_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
            }else{
                res = HttlUtil.sendGet(api_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("GoogleMapsApiUtil getGeoCodeInfo res="+res);
            geoCodeInfo.setSource(source);
            JSONObject json = JSONObject.parseObject(res);
            if(json.containsKey("results")){
                JSONArray reArray = (JSONArray) json.get("results");
                for(int i=0; i<reArray.size(); i++ ){
                    JSONObject rejson = reArray.getJSONObject(i);
                    String formattedAddress = (String) rejson.get("formatted_address");
                    geoCodeInfo.setFormattedAddress(formattedAddress);

                    JSONObject geometryJson = (JSONObject)rejson.get("geometry");
                    JSONObject locationJson = (JSONObject)geometryJson.get("location");
                    if(StringUtil.isEmpty(locationJson)){
                        break;
                    } else {
                        BigDecimal lat = null;
                        BigDecimal lng = null;
                        Integer latI = null;
                        Integer lngI = null;
                        try{
                            lat = (BigDecimal) locationJson.get("lat");
                        } catch (ClassCastException e){
                            latI = (Integer) locationJson.get("lat");
                        }
                        try{
                            lng = (BigDecimal) locationJson.get("lng");
                        } catch (ClassCastException e){
                            lngI = (Integer) locationJson.get("lng");
                        }
                        Position position = new Position();
                        if(!StringUtil.isEmpty(lat)){
                            position.setLat(lat.doubleValue());
                        } else if(!StringUtil.isEmpty(latI)){
                            position.setLat(latI.doubleValue());
                        }
                        if(!StringUtil.isEmpty(lng)){
                            position.setLon(lng.doubleValue());
                        } else if(!StringUtil.isEmpty(lngI)){
                            position.setLon(lngI.doubleValue());
                        }
                        geoCodeInfo.setPosition(position);
                        logger.info("longitude_x="+position.getLon()+" latitude_y="+position.getLat()+" address ="+address);

                    }
                }
            }
            return geoCodeInfo;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }

    public CityInfo getCityFromOSM(Double latitude_y,Double longitude_x,String language)  throws HttpInternalServerError {
        try {
            boolean isRequest = false;
            String cityname = "";
            CityInfo cityInfo = new CityInfo();
            StringBuffer params = new StringBuffer();
            params.append("addressdetails=").append(addressDetails).append("&format=json").append("&zoom=").append(zoom); //使用普通http工具的请求法

            if(StringUtil.isEmpty(latitude_y)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(longitude_x)){
                isRequest = true;
            }

            if(!StringUtil.isEmpty(language)){
                params.append("&accept-language=").append(language);
            } else {
                Locale locale = LocaleContextHolder.getLocale();
                if(!StringUtil.isEmpty(locale.getLanguage())){
                    params.append("&accept-language=").append(locale.getLanguage());
                } else {
                    params.append("&accept-language=en");
                }
            }
            logger.info("OpenStreetMap getCityFromOSM param:"+params.toString());
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            params.append("&lat=").append(latitude_y);
            params.append("&lon=").append(longitude_x);

            String res;
            res = HttlUtil.sendGet(openstreetmap_api_url, params.toString(), false, null, null);

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("OpenStreetMap getCityFromOSM res="+res);
            Position position = new Position(latitude_y,longitude_x);
            cityInfo.setPosition(position);
            cityInfo.setSource(source);
            JSONObject json = JSONObject.parseObject(res);
            String formattedAddress = (String) json.get("display_name");
            JSONObject addressjson = json.getJSONObject("address");
            cityname = addressjson.getString("city");
            cityInfo.setFormattedAddress(formattedAddress);
            cityInfo.setCityName(cityname);

            logger.info("longitude_x="+longitude_x+" latitude_y="+latitude_y+" formattedAddress="+formattedAddress);
            return cityInfo;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }

    public GeoCodeInfo getGeoCodeInfoFormOSM(String address,String language) throws HttpInternalServerError {
        try {
            GeoCodeInfo geoCodeInfo = new GeoCodeInfo();
            boolean isRequest = false;
            StringBuffer params = new StringBuffer();
            params.append("addressdetails=").append(addressDetails).append("&format=json"); //使用普通http工具的请求法
            params.append("&q=");
            params.append(address);
            if(!StringUtil.isEmpty(language)){
                params.append("&accept-language=").append(language);
            } else {
                Locale locale = LocaleContextHolder.getLocale();
                if(!StringUtil.isEmpty(locale.getLanguage())){
                    params.append("&accept-language=").append(locale.getLanguage());
                } else {
                    params.append("&accept-language=en");
                }
            }
            logger.info("OpenStreetMap getGeoCodeInfoFormOSM param:"+params.toString());
            if(StringUtil.isEmpty(address)){
                isRequest = true;
            }
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            String res;
            res = HttlUtil.sendGet(openstreetmap_searchapi_url, params.toString(), false, null, null);

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("OpenStreetMap getGeoCodeInfoFormOSM res="+res);
            geoCodeInfo.setSource(source);
            JSONArray reArray = JSONArray.parseArray(res);
            for(int i=0; i<reArray.size(); i++ ) {
                JSONObject rejson = reArray.getJSONObject(i);
                String formattedAddress = (String) rejson.get("display_name");
                geoCodeInfo.setFormattedAddress(formattedAddress);
                String lat = (String) rejson.get("lat");;
                String lon = (String) rejson.get("lon");;
                Position position = new Position();
                if(!StringUtil.isEmpty(lat)){
                    position.setLat(Double.parseDouble(lat));
                }
                if(!StringUtil.isEmpty(lon)){
                    position.setLon(Double.parseDouble(lon));
                }
                geoCodeInfo.setPosition(position);
                logger.info("longitude_x="+position.getLon()+" latitude_y="+position.getLat()+" address ="+address);
                if(!StringUtil.isEmpty(lon)){
                    break;
                }
            }
            return geoCodeInfo;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }
}
