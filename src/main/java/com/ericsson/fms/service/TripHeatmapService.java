package com.ericsson.fms.service;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ericsson.fms.utils.RestTemplateUtil;
import com.ericsson.fms.utils.StringUtil;

@Service
public class TripHeatmapService{
	public static final Logger logger = LoggerFactory.getLogger(TripHeatmapService.class);
	@Value("${trip.heatmap.userpwd}")
	private String userpwd;
	@Value("${trip.heatmap.server}")
    private String url;


	public String getTipHeatmap(Map<String,Object> param){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", userpwd);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        String params = new String("");
        params = params + "?type="+(String)param.get("type");
        params = params + "&startTime="+(String)param.get("startTime");
        params = params + "&endTime="+(String)param.get("endTime");
        
        Integer precision = (Integer)param.get("precision");
        if(!StringUtil.isEmpty(precision)){
        	params = params + "&precision="+precision;
		}
		String oemId = (String)param.get("oemId");
		if(!StringUtil.isEmpty(oemId)){
			params = params + "&oemId="+oemId;
		}
        ArrayList vehicleTypes = (ArrayList)param.get("vehicleTypes");
        if(!StringUtil.isEmpty(vehicleTypes)){
        	StringBuffer vts = new StringBuffer("");
        	for(Object v:vehicleTypes){
        		vts.append(v).append(",");
        	}
        	String vt = vts.toString();
        	vt = vt.substring(0,vt.length()-1);
        	params = params + "&vehicleTypes="+vt;
        }
        ArrayList enterpriseIds = (ArrayList)param.get("enterpriseIds");
        if(!StringUtil.isEmpty(enterpriseIds)){
        	StringBuffer vts = new StringBuffer();
        	for(Object v:enterpriseIds){
        		vts.append(v).append(",");
        	}
        	String vt = vts.toString();
        	vt = vt.substring(0,vt.length()-1);
        	params = params + "&enterpriseIds="+vt;
        }
		ArrayList enterpriseTypes = (ArrayList)param.get("enterpriseTypes");
		if(!StringUtil.isEmpty(enterpriseTypes)){
        	StringBuffer vts = new StringBuffer();
        	for(Object v:enterpriseTypes){
        		vts.append(v).append(",");
        	}
        	String vt = vts.toString();
        	vt = vt.substring(0,vt.length()-1);
        	params = params + "&enterpriseTypes="+vt;
        }
		
        String topLefts = (String)param.get("topLeft");
		String bottomRights = (String)param.get("bottomRight");
		if(!StringUtil.isEmpty(topLefts)){
			String[] topLeftObject = topLefts.split(",");
			params = params + "&topLeft.lat="+Double.valueOf(topLeftObject[0]);
			params = params + "&topLeft.lon="+Double.valueOf(topLeftObject[1]);
		}
		if(!StringUtil.isEmpty(bottomRights)){
			String[] bottomRightObject = bottomRights.split(",");
			params = params + "&bottomRight.lat="+Double.valueOf(bottomRightObject[0]);
			params = params + "&bottomRight.lon="+Double.valueOf(bottomRightObject[1]);
		}
		
		logger.info("url={}",url+params);
		ResponseEntity<String> response = RestTemplateUtil.getIntance().exchange(url+params,HttpMethod.GET,entity,String.class);
        return response.getBody();
	}
	
}
