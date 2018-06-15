package com.ericsson.fms.utils;

import org.springframework.web.client.RestTemplate;

/**
 * Created by ewwuyyu on 2017/7/19.
 */
public class RestTemplateUtil {

    private static final RestTemplate restTemplate = new RestTemplate();

    private RestTemplateUtil(){ }

    public static RestTemplate getIntance(){
//        restTemplate.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new HeaderInterceptor()));
        return restTemplate;
    }

}
