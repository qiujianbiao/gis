package com.ericsson.fms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.entity.SpeedLimits;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.utils.HttlUtil;
import com.ericsson.fms.utils.RedisComponent;
import com.ericsson.fms.utils.RedisKey;
import com.ericsson.fms.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ejioqiu on 11/9/2017.
 */
@Service
public class GisMapSpeedLimitsService {
    public static final Logger logger = LoggerFactory.getLogger(GisMapSpeedLimitsService.class);
    @Value("${gismap.google.api_speed_url}")
    private String api_speed_url;
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

    private double loblock = 0.0001;
    private double lablock = 0.0001;

    @Value("${gismap.redis.value.keepdays}")
    private long redisKeepDays = 90;

    @Autowired
    private RedisComponent redisComponent;

    private String getLimits(List<String> inputList) throws HttpInternalServerError{
        try {
            if (inputList == null || inputList.size() == 0 || inputList.size() > 100) {
                return null;
            }
            StringBuffer params = new StringBuffer();
            params.append("key=").append(this.api_key); //使用普通http工具的请求法
            params.append("&path=");
            for(int i=0; i<inputList.size();i++){
                params.append(inputList.get(i));
                if(i+1 != inputList.size()){
                    params.append("|");
                }
            }
            logger.info("GoogleMapsApiUtil getLimits param:"+params.toString());
            //使用普通http工具的请求法
            String res;
            if(this.proxy_switch!= null && "1".equals(this.proxy_switch)){
                logger.info("proxy info:proxy_ip="+proxy_ip+" proxy_port="+proxy_port);
                res = HttlUtil.sendGet(this.api_speed_url, params.toString(), true, this.proxy_ip, Integer.parseInt(this.proxy_port));
            }else{
                res = HttlUtil.sendGet(this.api_speed_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("GoogleMapsApiUtil getLimits res="+res);
            return res;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }


    /**
     *
     * <p>Description: 根据经度，纬度获取该处限速数据<p>
     * @param longitude_x 经度
     * @param latitude_y 纬度
     * @return limitspeed 限速速度（找不到或失败返回-1）
     * @author huwei
     * @date 2017年8月14日下午2:40:14
     */
    public SpeedLimits getLimitSpeed(Double latitude_y, Double longitude_x) throws HttpInternalServerError {
        try {
            SpeedLimits speedLimits = new SpeedLimits();
            Integer speed = -1;
            String res = getLimits(Arrays.asList(longitude_x+","+latitude_y));
            if(!StringUtil.isEmpty(res)){
                JSONObject resJson =JSONObject.parseObject(res);
                JSONArray limitJsonArr = resJson.getJSONArray("speedLimits");
                if(limitJsonArr == null||!(limitJsonArr.size() > 0)){
                    logger.info("GoogleMapsApiUtil.getLimitSpeed get speedLimits from google fail");
                }else{
                    JSONObject limitItem = limitJsonArr.getJSONObject(0);
                    speed = limitItem.getInteger("speedLimit");
                    String placeId = limitItem.getString("placeId");
                    String units = limitItem.getString("units");
                    speedLimits.setPlaceId(placeId);
                    speedLimits.setSource(source);
                    speedLimits.setSpeedLimit(speed);
                    speedLimits.setUnits(units);

                    logger.info(String.format("GET LIMIT SPEED {longitude_X=%s,latitude_Y=%s,speed=%s}", longitude_x, latitude_y, speed));
                }
            }else{
                logger.info("GoogleMapsApiUtil.getLimitSpeed get result from google fail:");
            }
            return speedLimits;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }


    /**
     *
     * <p>Description: 根据经度，纬度获取该处限速数据
     *                 <br/>先去redis中取，若有直接返回，若没有再调用google api去查询限速，并将查询回来的限速放入redis中<p>
     * @param longitude_x 经度
     * @param latitude_y 纬度
     * @return limitspeed 限速速度（找不到或失败返回空字符串）
     * @author huwei
     * @date 2017年8月14日下午2:40:14
     */
    public String getLimitSpeedValue(Double longitude_x,Double latitude_y){
        String speed = "";
        SpeedLimits speedLimits = new SpeedLimits();
        try{
            String redisKey = "";
            redisKey = RedisKey.getSpeedRedisKey(longitude_x, latitude_y, this.loblock, this.lablock);
            speed = redisComponent.get(redisKey);
            if(!StringUtil.isEmpty(speed)) {
                logger.info(String.format("SPEED REDIS GET{longitude_X=%s,latitude_Y=%s,redisKey=%s,speed=%s}", longitude_x, latitude_y, redisKey, speed));
            }else{
                logger.info("REDIS GET SPEED EMPTY,TURN TO GOOGLE API");
                speedLimits = getLimitSpeed(longitude_x, latitude_y);
                if(!StringUtil.isEmpty(speedLimits)){
                    speed = String.valueOf(speedLimits.getSpeedLimit());
                    logger.info("REDIS SET key="+redisKey+" value="+speed+" keepdays="+redisKeepDays);
                    redisComponent.set(redisKey, speed, redisKeepDays);
                }
            }
        }catch(Exception e){
            logger.warn("ProcessKFKMsgTdsService.getLimitSpeed fail",e);
        }
        return speed;
    }
}
