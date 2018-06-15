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

import java.util.*;

/**
 * Created by ejioqiu on 11/9/2017.
 */
@Service
public class GisMapDistancematrixService {
    public static final Logger logger = LoggerFactory.getLogger(GisMapDistancematrixService.class);
    @Value("${gismap.google.api_distancematrix_url}")
    private String api_distancematrix_url;
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

    public Distancematrix getDistancematrix(QueryDistancematrixBody queryDistancematrixBody)  throws HttpInternalServerError {
        try {
            boolean isRequest = false;
            if(StringUtil.isEmpty(queryDistancematrixBody)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(queryDistancematrixBody.getMatrix())){
                isRequest = true;
            }
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }
            String origins = "";
            String destinations = "";
            Matrix[] matrixs = queryDistancematrixBody.getMatrix();
            for(Matrix m:matrixs){
                origins = origins + m.getOrigin() + "|";
                destinations = destinations + m.getDestination() + "|";
            }

            Distancematrix distancematrix = new Distancematrix();

            StringBuffer params = new StringBuffer();
            params.append("key=").append(api_key); //使用普通http工具的请求法
            params.append("&origins=");
            params.append(origins.substring(0,origins.length()-1));
            params.append("&destinations=");
            params.append(destinations.substring(0,destinations.length()-1));
            if(!StringUtil.isEmpty(queryDistancematrixBody.getDepartureTime())){
                String departureTime = queryDistancematrixBody.getDepartureTime();
                departureTime = departureTime.replace("z","").replace("Z","");
                departureTime = departureTime + " UTC" ;
                String dt = StringUtil.dateToStamp(departureTime);
                params.append("&departure_time=").append(dt);
            }
            if(!StringUtil.isEmpty(queryDistancematrixBody.getMode())){
                params.append("&mode=").append(queryDistancematrixBody.getMode());
            }
            if(!StringUtil.isEmpty(queryDistancematrixBody.getLanguage())){
                params.append("&language=").append(queryDistancematrixBody.getLanguage());
            } else {
                Locale locale = LocaleContextHolder.getLocale();
                if(!StringUtil.isEmpty(locale.getLanguage())){
                    params.append("&language=").append(locale.getLanguage());
                } else {
                    params.append("&language=en");
                }
            }
            logger.info("GoogleMapsApiUtil getDistancematrix param:"+params.toString());



            String res;
            if(proxy_switch != null && "1".equals(proxy_switch)){
                res = HttlUtil.sendGet(api_distancematrix_url, params.toString(), true, proxy_ip, Integer.parseInt(proxy_port));
            }else{
                res = HttlUtil.sendGet(api_distancematrix_url, params.toString(), false, null, null);
            }

            if (StringUtil.isEmpty(res)){
                throw new HttpInternalServerError("Internal server error");
            }


            logger.info("GoogleMapsApiUtil getDistancematrix res="+res);
            JSONObject json = JSONObject.parseObject(res);
            JSONArray desaddArray = (JSONArray)json.get("destination_addresses");
            String dist = "";
            if(desaddArray!=null && desaddArray.size()!=0){
                String[] dess = new String[desaddArray.size()];
                for(int k=0; k<desaddArray.size(); k++ ){
                    dess[k] = (String) desaddArray.getString(k);
                    dist = dist + dess[k] + ";";
                }
            }

            JSONArray orgaddArray = (JSONArray)json.get("origin_addresses");
            String org = "";
            if(orgaddArray!=null && orgaddArray.size()!=0){
                String[] orgs = new String[orgaddArray.size()];

                for(int k=0; k<orgaddArray.size(); k++ ){
                    orgs[k] = (String) orgaddArray.getString(k);
                    org = org + orgs[k] + ";";
                }
            }
            MatrixResponse[] matrixDestArray = new MatrixResponse[0];
            MatrixResponse[] matrixOriginArray = new MatrixResponse[0];
            List<MatrixResponse> matrixList = new ArrayList<MatrixResponse>();

            Map<String,MatrixResponse> matrixMap = new HashMap<String,MatrixResponse>();

            if(!StringUtil.isEmpty(dist)){
                String[] dis = dist.split(";");
                matrixDestArray = new MatrixResponse[dis.length];
                for (int i=0; i<dis.length; i++ ){
                    MatrixResponse ms = new MatrixResponse();
                    ms.setDestinationAddress(dis[i]);
                    matrixDestArray[i] = ms;
                }
            }
            if(!StringUtil.isEmpty(org) ){
                String[] orgs = org.split(";");
                matrixOriginArray = new MatrixResponse[orgs.length];
                for (int i=0; i<orgs.length; i++ ){
                    MatrixResponse ms = new MatrixResponse();
                    ms.setOriginAddresses(orgs[i]);
                    matrixOriginArray[i] = ms;
                }
            }

            distancematrix.setSource("google");
            if(json.containsKey("rows")){
                JSONArray reArray = (JSONArray) json.get("rows");
                for(int i=0; i<reArray.size(); i++ ){
                    JSONObject rejson = reArray.getJSONObject(i);
                    JSONArray elementsArray = (JSONArray)rejson.get("elements");
                    for(int j=0; j<elementsArray.size(); j++ ){
                        JSONObject eljson = elementsArray.getJSONObject(j);
                        JSONObject distanceJson = (JSONObject)eljson.get("distance");
                        String dtext = (String) distanceJson.get("text");
                        Integer dvalue = (Integer) distanceJson.get("value");
                        JSONObject durationJson = (JSONObject)eljson.get("duration");
                        String dutext = (String) durationJson.get("text");
                        Integer duvalue = (Integer) durationJson.get("value");

                        MatrixResponse mr = new MatrixResponse();
                        mr.setDistance(dvalue);
                        mr.setDuration(duvalue);
                        mr.setOriginAddresses(matrixOriginArray[i].getOriginAddresses());
                        mr.setDestinationAddress(matrixDestArray[j].getDestinationAddress());
//                        matrixList.add(mr);
                        //一个源，一个目的的地址返回的结果过滤
                        if((i-j) == 0){
                            matrixMap.put(matrixOriginArray[i].getOriginAddresses()+matrixDestArray[j].getDestinationAddress(),mr);
                        }
                    }
                }
                MatrixResponse[] matrixResponseArray = new MatrixResponse[matrixMap.size()];
//                matrixList.toArray(matrixResponse);
                int i = 0;
                for (Map.Entry<String, MatrixResponse> entry : matrixMap.entrySet()) {
                    matrixResponseArray[i] = (MatrixResponse) entry.getValue();
                    i++;
                }
                distancematrix.setMatrix(matrixResponseArray);
            }
            return distancematrix;
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new HttpInternalServerError("Internal server error");
        }
    }
}
