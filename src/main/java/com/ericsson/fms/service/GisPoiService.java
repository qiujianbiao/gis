package com.ericsson.fms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ericsson.fms.constants.HttpErrorCode;
import com.ericsson.fms.dao.GisPoiDao;
import com.ericsson.fms.domain.GisPoiVo;
import com.ericsson.fms.domain.PoiBean;
import com.ericsson.fms.entity.BatchcreateResult;
import com.ericsson.fms.exception.http.HttpClientException;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.utils.StringUtil;

/**
 * Created by ejioqiu on 4/26/2018.
 */

@Service
public class GisPoiService {
    public static final Logger logger = LoggerFactory.getLogger(GisPoiService.class);

    @Resource
    private GisPoiDao gisPoiDao;

    public List<PoiBean> getPoiInfo(Map<String, Object> queryMap) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            boolean isRequest = false;
            String[] enterpriseIdArrays = null;
            String[] tagsArrays = null;

            if(StringUtil.isEmpty(queryMap)){
                isRequest = true;
            }
            if(StringUtil.isEmpty(queryMap.get("geomType"))){
                isRequest = true;
            }
            if(isRequest){
                throw new HttpException(HttpStatus.BAD_REQUEST, HttpErrorCode.MISS_PARAMETER,"Missing request parameters");
            }

            ArrayList tags = (ArrayList)queryMap.get("tags");
            ArrayList enterpriseIds = (ArrayList)queryMap.get("enterpriseIds");
            if(!StringUtil.isEmpty(enterpriseIds)){
                enterpriseIdArrays = new String[enterpriseIds.size()];
                enterpriseIds.toArray(enterpriseIdArrays);
                List<String> list = new ArrayList<>();
                for(String e:enterpriseIdArrays){
                    list.add(e);
                }
                queryMap.put("enterpriseIds",enterpriseIdArrays);
            }
            if(!StringUtil.isEmpty(tags)){
                tagsArrays = new String[tags.size()];
                tags.toArray(tagsArrays);
                List<String> list = new ArrayList<>();
                for(String e:tagsArrays){
                    list.add(e);
                }
                queryMap.put("tags",tagsArrays);
            }
            buildGeomTypeQuery(queryMap);
            logger.info("getPoiInfo param :"+mapper.writeValueAsString(queryMap));
            List<PoiBean> list = gisPoiDao.getPoiInfo(queryMap);
            return list;
        } catch (HttpException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpInternalServerError("Internal server error");
        }
    }



    private void buildGeomTypeQuery(Map<String, Object> queryMap) throws HttpClientException {
        if (queryMap.get("geomType") != null) {
            Map<String, Object> geofence = (Map<String, Object>) queryMap.get("geomType");
            String shape = (String) geofence.get("shape");
            if ("Line".equals(shape) || "Point".equals(shape)) {
                Map<String, Object> center = (Map<String, Object>) geofence.get("center");
                Double lat = Double.parseDouble(center.get("lat").toString());
                Double lon = Double.parseDouble(center.get("lon").toString());
                queryMap.put("lon",lon);
                queryMap.put("lat",lat);
            } else if ("Circle".equals(shape)) {
                Float radius = Float.parseFloat(geofence.get("radius").toString());
                Map<String, Object> center = (Map<String, Object>) geofence.get("center");
                Double lat = Double.parseDouble(center.get("lat").toString());
                Double lon = Double.parseDouble(center.get("lon").toString());
                queryMap.put("lon",lon);
                queryMap.put("lat",lat);
                queryMap.put("circleRadius",radius);
            } else if ("Polygon".equals(shape)) {
                String pgStr = "";
                List<Map<String, Object>> vertices = (List<Map<String, Object>>) geofence.get("vertices");
                for (Map<String, Object> vertice : vertices) {
                    Double lat = Double.parseDouble(vertice.get("lat").toString());
                    Double lon = Double.parseDouble(vertice.get("lon").toString());
                    pgStr = pgStr + lon + " " + lat + ",";
                }
                if(!StringUtil.isEmpty(pgStr)){
                    pgStr = pgStr.substring(0,pgStr.length()-1);
                }
                queryMap.put("polygonStr",pgStr);
            }
            queryMap.put("geomType",shape);
        }
    }



    public BatchcreateResult insertPoiByBatch(List<GisPoiVo> list) throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisPoiDao.insertPoiByBatch(list);
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("GisPoiService.insertPoiByBatch error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult updPoi(GisPoiVo vo) throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisPoiDao.updPoi(vo);
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("GisPoiService.updPoi error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult delPoi(String ids) throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisPoiDao.delPoi(ids);
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("GisPoiService.delPoi error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }
}
