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

@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GisHeatMapControllerTest {
	@Autowired
    private GisHeatMapController gisHeatMapController;
	private MockMvc mockMvc;

	@Before
	public void setup(){
		mockMvc = MockMvcBuilders.standaloneSetup(gisHeatMapController).build();
	}

	@Test
    public void testGetGisHeatmap() throws Exception{
		String requestJson = "{\"type\":\"vehicle-location\",\"precision\":6,\"startTime\":\"2017-10-17T10:00:00.000Z\",\"endTime\":\"2017-12-17T11:00:00.000Z\",\"bottomRight\":\"24.934389405025676,55.81054687500001\",\"enterpriseIds\":[\"4028834c5aff8ae7015b13b0c481001e\"],\"enterpriseTypes\":[\"Taxi\",\"Limo\",\"Inspector\",\"Truck\",\"Bus\"],\"topLeft\":\"25.46559428893416,55.01953125000001\"}";

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/gis/heatmap/v1/heatmap-grids").accept(MediaType.APPLICATION_JSON)
				.header("Accept-Encoding","dd")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		Assert.assertTrue("success", status == 200);
		Assert.assertFalse("false", status != 200);
		System.out.println("status:"+status);
		System.out.println("return:" + content);    }
}
