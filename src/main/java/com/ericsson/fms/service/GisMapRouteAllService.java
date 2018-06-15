package com.ericsson.fms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.*;
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
public class GisMapRouteAllService {
    public static final Logger logger = LoggerFactory.getLogger(GisMapRouteAllService.class);
    @Value("${gismap.google.api_route_url}")
    private String api_route_url;
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

    public RouteMessage getRoute(Double originLat, Double originLon,Double destinationLat, Double destinationLon, String mode, String units,String departureTime,String language)  throws HttpInternalServerError {
        try {
            boolean isRequest = false;
            RouteMessage routeMessage = new RouteMessage();
            StringBuffer params = new StringBuffer();
            params.append("key=").append(api_key); //使用普通http工具的请求法
            if(!StringUtil.isEmpty(originLat) && !StringUtil.isEmpty(originLon) ){
                params.append("&origin=");
                params.append(originLat+","+originLon);
            } else {
                isRequest = true;
            }
            if(!StringUtil.isEmpty(destinationLat) && !StringUtil.isEmpty(destinationLon) ){
                params.append("&destination=");
                params.append(destinationLat+","+destinationLon);
            } else {
                isRequest = true;
            }

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

            if(!StringUtil.isEmpty(departureTime) && !StringUtil.isEmpty(departureTime) ){
                departureTime = departureTime.replace("z","").replace("Z","");
                departureTime = departureTime + " UTC" ;
                String dt = StringUtil.dateToStamp(departureTime);
                params.append("&departure_time=").append(dt);

            }

            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }
            logger.info("GoogleMapsApiUtil getRoute param:"+params.toString());

            String res;
            if(proxy_switch != null && "1".equals(proxy_switch)){
                res = HttlUtil.sendGet(api_route_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
            }else{
                res = HttlUtil.sendGet(api_route_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("GoogleMapsApiUtil getRoute res="+res);

            JSONObject json = JSONObject.parseObject(res);
            if(json.containsKey("routes")){
                JSONArray routesArray = (JSONArray) json.get("routes");
                for(int i=0; i<routesArray.size(); i++ ){
                    JSONObject rejson = routesArray.getJSONObject(i);
                    JSONObject boundsJson = (JSONObject)rejson.get("bounds");

                    JSONObject northeastJson = (JSONObject)boundsJson.get("northeast");
                    BigDecimal northeast_lat = (BigDecimal) northeastJson.get("lat");
                    BigDecimal northeast_lng = (BigDecimal) northeastJson.get("lng");
                    Position northeast_position = new Position();
                    northeast_position.setLat(northeast_lat.doubleValue());
                    northeast_position.setLon(northeast_lng.doubleValue());
                    JSONObject southwestJson = (JSONObject)boundsJson.get("southwest");
                    BigDecimal southwest_lat = (BigDecimal) southwestJson.get("lat");
                    BigDecimal southwest_lng = (BigDecimal) southwestJson.get("lng");
                    Position southwest_position = new Position();
                    southwest_position.setLat(southwest_lat.doubleValue());
                    southwest_position.setLon(southwest_lng.doubleValue());

                    Bounds bounds = new Bounds();
                    bounds.setNortheast(northeast_position);
                    bounds.setSouthwest(southwest_position);

                    routeMessage.setBounds(bounds);

                    JSONArray legsArray = (JSONArray)rejson.get("legs");
                    Legs[] legss = new Legs[legsArray.size()];
                    for(int j=0; j<legsArray.size(); j++ ){
                        Legs legs = new Legs();
                        JSONObject eljson = legsArray.getJSONObject(j);

                        JSONObject distanceJson = (JSONObject)eljson.get("distance");
                        String dtext = (String) distanceJson.get("text");
                        Integer dvalue = (Integer) distanceJson.get("value");
                        DistanceAndDuration distance = new DistanceAndDuration();
                        distance.setText(dtext);
                        distance.setValue(dvalue);
                        legs.setDistance(distance);

                        JSONObject durationJson = (JSONObject)eljson.get("duration");
                        String dutext = (String) durationJson.get("text");
                        Integer duvalue = (Integer) durationJson.get("value");
                        DistanceAndDuration duration = new DistanceAndDuration();
                        duration.setText(dutext);
                        duration.setValue(duvalue);
                        legs.setDuration(duration);

                        JSONObject slocationJson = (JSONObject)eljson.get("start_location");
                        BigDecimal s_lat = (BigDecimal) slocationJson.get("lat");
                        BigDecimal s_lng = (BigDecimal) slocationJson.get("lng");
                        Position s_position = new Position();
                        s_position.setLat(s_lat.doubleValue());
                        s_position.setLon(s_lng.doubleValue());
                        legs.setStartLocation(s_position);

                        JSONObject elocationJson = (JSONObject)eljson.get("end_location");
                        BigDecimal e_lat = (BigDecimal) elocationJson.get("lat");
                        BigDecimal e_lng = (BigDecimal) elocationJson.get("lng");
                        Position e_position = new Position();
                        e_position.setLat(e_lat.doubleValue());
                        e_position.setLon(e_lng.doubleValue());
                        legs.setEndLocation(e_position);

                        String startAddress = (String) eljson.get("start_address");
                        legs.setStartAddress(startAddress);
                        String endAddress = (String) eljson.get("end_address");
                        legs.setEndAddress(endAddress);

                        JSONArray stepsArray = (JSONArray)eljson.get("steps");

                        Steps[] stepss = new Steps[stepsArray.size()];
                        for(int k=0; k<stepsArray.size(); k++ ){
                            Steps steps = new Steps();
                            JSONObject stepsjson = stepsArray.getJSONObject(k);

                            JSONObject distanceJson2 = (JSONObject)stepsjson.get("distance");
                            String dtext2 = (String) distanceJson2.get("text");
                            Integer dvalue2 = (Integer) distanceJson2.get("value");
                            DistanceAndDuration distance2 = new DistanceAndDuration();
                            distance2.setText(dtext2);
                            distance2.setValue(dvalue2);
                            steps.setDistance(distance2);

                            JSONObject durationJson2 = (JSONObject)stepsjson.get("duration");
                            String dutext2 = (String) durationJson2.get("text");
                            Integer duvalue2 = (Integer) durationJson2.get("value");
                            DistanceAndDuration duration2 = new DistanceAndDuration();
                            duration2.setText(dutext2);
                            duration2.setValue(duvalue2);
                            steps.setDuration(duration2);

                            JSONObject slocationJson2 = (JSONObject)stepsjson.get("start_location");
                            BigDecimal s_lat2 = (BigDecimal) slocationJson2.get("lat");
                            BigDecimal s_lng2 = (BigDecimal) slocationJson2.get("lng");
                            Position s_position2 = new Position();
                            s_position2.setLat(s_lat2.doubleValue());
                            s_position2.setLon(s_lng2.doubleValue());
                            steps.setStartLocation(s_position2);

                            JSONObject elocationJson2 = (JSONObject)stepsjson.get("end_location");
                            BigDecimal e_lat2 = (BigDecimal) elocationJson2.get("lat");
                            BigDecimal e_lng2 = (BigDecimal) elocationJson2.get("lng");
                            Position e_position2 = new Position();
                            e_position2.setLat(e_lat2.doubleValue());
                            e_position2.setLon(e_lng2.doubleValue());
                            steps.setEndLocation(e_position2);

                            Polyline polyline = new Polyline();
                            JSONObject polylineJson = (JSONObject)stepsjson.get("polyline");
                            String points = (String) polylineJson.get("points");
                            polyline.setPoints(points);
                            steps.setPolyline(polyline);

                            String htmlInstructions = (String) stepsjson.get("html_instructions");
                            steps.setHtmlInstructions(htmlInstructions);
                            String travelMode = (String) stepsjson.get("travel_mode");
                            steps.setTravelMode(travelMode);

                            stepss[k] = steps;
                        }
                        legs.setSteps(stepss);
                        legss[j] = legs;
                    }
                    routeMessage.setLegs(legss);
                    routeMessage.setSource(source);
                    String summary = (String) rejson.get("summary");
                    routeMessage.setSummary(summary);

                    Polyline allpolyline = new Polyline();
                    JSONObject overviewPolylineJson = (JSONObject)rejson.get("overview_polyline");
                    String ovpoints = (String) overviewPolylineJson.get("points");
                    allpolyline.setPoints(ovpoints);
                    routeMessage.setOverviewPolyline(allpolyline);
                }
            }

            return routeMessage;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }
}
