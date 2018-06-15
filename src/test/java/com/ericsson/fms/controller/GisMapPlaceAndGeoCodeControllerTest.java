package com.ericsson.fms.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by ejioqiu on 11/17/2017.
 */
@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GisMapPlaceAndGeoCodeControllerTest {
    @Autowired
    private GisMapPlaceAndGeoCodeController gisMapPlaceAndGeoCodeController;
    private MockMvc mockMvc;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(gisMapPlaceAndGeoCodeController).build();
    }

    @Test
    public void testGetCity() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/gis/maps/v1/place").accept(MediaType.APPLICATION_JSON)
                .param("lat","-81.33992")
                .param("lon","41.48206"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("success", status == 200);
        Assert.assertFalse("false", status != 200);
        System.out.println("result："+status);
        System.out.println("content：" + content);
    }

    @Test
    public void testGetGeoCodeInfo() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/gis/maps/v1/geocoding").accept(MediaType.APPLICATION_JSON)
                .param("address","Antarctica"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("success", status == 200);
        Assert.assertFalse("false", status != 200);
        System.out.println("status:"+status);
        System.out.println("return:" + content);
    }
}
