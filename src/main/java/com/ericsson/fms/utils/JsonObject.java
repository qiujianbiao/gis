package com.ericsson.fms.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ericsson.fms.entity.EsBean;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;

/**
 * 对ObjectMapper重新封装, 方便调用
 *
 * 用法:
 *   1. JsonObject.toJSONString()
 *   2. JsonObject.toBean()
 *   3. JsonObject.mapper.writeValueAsString()
 *   ...
 */
public class JsonObject extends JSON{
//    public static final JsonObject mapper = new JsonObject();
//    static {
//        JSON.DEFFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
//        mapper.setDateFormat(new SimpleDateFormat("yyyyMMddHHmmss"));
//        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.DEFAULT);
//        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }

    public JsonObject(){
        super();
    }

    public static String toJSONString(Object o){
        return JSON.toJSONString(o,SerializerFeature.DisableCircularReferenceDetect);
//        return mapper.writeValueAsString(o);
    }

    public static <T> T toBean(String json, Class clazz){
        return (T)JSON.parseObject(json,clazz, Feature.DisableCircularReferenceDetect);
//        return (T)mapper.readValue(json, clazz);
    }

    public static void main(String[] args){
        String ss = "{\"took\":6,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"failed\":0},\"hits\":{\"total\":2,\"max_score\":null,\"hits\":[{\"_index\":\"gis-2018-01-02\",\"_type\":\"gisdata\",\"_id\":\"AWC06ThyVc57xwQn17uk\",\"_score\":null,\"_source\":{\"enterpriseId\":\"hghgh-a60d-545-941f-454\",\"oemId\":\"oem-a60d-545-941f-454\",\"enterpriseName\":\"ericsson\",\"position\":{\"lat\":51.12345,\"lon\":-2.12345},\"timestamp\":\"2018-01-02T06:38:26.646Z\",\"vehicleType\":\"TRMM\",\"vin\":\"JTHBK1GG6G2226754\"},\"sort\":[1514875106646,\"gisdata#AWC06ThyVc57xwQn17uk\"]},{\"_index\":\"gis-2018-01-02\",\"_type\":\"gisdata\",\"_id\":\"AWC06VO_Vc57xwQn17ul\",\"_score\":null,\"_source\":{\"enterpriseId\":\"hghgh-a60d-545-941f-454\",\"oemId\":\"oem-a60d-545-941f-454\",\"enterpriseName\":\"ericsson\",\"position\":{\"lat\":51.12345,\"lon\":-2.12345},\"timestamp\":\"2018-01-02T06:38:26.646Z\",\"vehicleType\":\"TRMM\",\"vin\":\"JTHBK1GG6G2226754\"},\"sort\":[1514875106646,\"gisdata#AWC06VO_Vc57xwQn17ul\"]}]}}\n";
        JSONObject topJson = JSON.parseObject(ss);
        System.out.println("topJson="+topJson);
        JSONObject basicJson = ((JSONObject)topJson.getJSONObject("hits"));
        System.out.println("basicJson="+basicJson);
        if(!StringUtil.isEmpty(basicJson)){
            JSONArray inputArray = ((JSONArray)basicJson.getJSONArray("hits"));
            Integer total = ((Integer)basicJson.getInteger("total"));
            System.out.println("total="+total);
            if(!StringUtil.isEmpty(inputArray)) {
                for (int i = 0; i < inputArray.size(); i++) {
                    JSONObject termJson = inputArray.getJSONObject(i);
                    System.out.println("termJson=" + termJson);
                    JSONObject sourceJson = termJson.getJSONObject("_source");
                    System.out.println("sourceJson=" + sourceJson);

                    String idJson = termJson.getString("_id");
                    System.out.println("idJson=" + idJson);
                    JSONArray sortArray = ((JSONArray)termJson.getJSONArray("sort"));
                    System.out.println("sortArray=" + sortArray);
                    if(sortArray!=null && sortArray.size()==2){
                        Long id = ((Long)sortArray.getLong(0));
                        String searchIndex = ((String)sortArray.getString(1));
                        System.out.println("id=" + id + ";searchIndex= " + searchIndex);
                    }
                }
            }
        }


    }

    public static StringBuilder appendSortValues(String json) {
        StringBuilder strSortValues = new StringBuilder();
        if (!StringUtil.isEmpty(json)) {
            Object[] arrSortValues = json.split(",");
            for (int i = 0; i < arrSortValues.length; i++) {
                strSortValues.append(i == 0 ? "" : ",");
                if (arrSortValues[i] != null) {
                    strSortValues.append(arrSortValues[i].toString().trim());
                }
            }
        }
        return strSortValues;
    }
}
