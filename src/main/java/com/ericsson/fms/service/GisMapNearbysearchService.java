package com.ericsson.fms.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.entity.Nearbysearch;
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

import java.util.Locale;

/**
 * Created by ejioqiu on 11/9/2017.
 */
@Service
public class GisMapNearbysearchService {
    public static final Logger logger = LoggerFactory.getLogger(GisMapNearbysearchService.class);
    @Value("${gismap.google.api_nearbysearch_url}")
    private String api_nearbysearch_url;
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

    public Nearbysearch getNearbysearch(Double latitude_y, Double longitude_x,Integer radius,String type,String language)  throws HttpInternalServerError {
        try {
            Nearbysearch nearbysearch = new Nearbysearch();
            boolean isRequest = false;
            StringBuffer params = new StringBuffer();
            params.append("key=").append(api_key); //使用普通http工具的请求法
            params.append("&location=");
            params.append(latitude_y+","+longitude_x);
            params.append("&radius=").append(radius);
            if(!StringUtil.isEmpty(type)){
                params.append("&type=").append(type);
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
            logger.info("GoogleMapsApiUtil getNearbysearch param:"+params.toString());

            if(StringUtil.isEmpty(latitude_y)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(longitude_x)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(radius)){
                isRequest = true;
            }
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            String res;
            if(proxy_switch != null && "1".equals(proxy_switch)){
                res = HttlUtil.sendGet(api_nearbysearch_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
            }else{
                res = HttlUtil.sendGet(api_nearbysearch_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }
            logger.info("GoogleMapsApiUtil getNearbysearch res="+res);
            Position position = new Position(latitude_y,longitude_x);
            nearbysearch.setPosition(position);
            nearbysearch.setSource(source);
            JSONObject json = JSONObject.parseObject(res);
            if(json.containsKey("results")){
                JSONArray reArray = (JSONArray) json.get("results");
                for(int i=0; i<reArray.size(); i++ ){
                    JSONObject rejson = reArray.getJSONObject(i);
                    String icon = (String) rejson.get("icon");
                    String name = (String) rejson.get("name");
                    String id = (String) rejson.get("id");
                    nearbysearch.setIcon(icon);
                    nearbysearch.setName(name);
                    nearbysearch.setId(id);
                    logger.info("longitude_x="+longitude_x+" latitude_y="+latitude_y+" name="+name);
                    if(!StringUtil.isEmpty(name)){
                        break;
                    }
                }
            }

            return nearbysearch;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }
}
