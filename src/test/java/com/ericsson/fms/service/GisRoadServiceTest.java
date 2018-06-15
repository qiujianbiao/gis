package com.ericsson.fms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by ejioqiu on 12/20/2017.
 */
@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GisRoadServiceTest {
    @MockBean
    private GisRoadService gisRoadService;

    @Test
    public void testGetGisHeatmap() throws Exception{
        String city = new String();
        Mockito.when(gisRoadService.getCityName(25.268682302509536, 55.27998447418213, "en")).thenReturn(city);
    }
}
