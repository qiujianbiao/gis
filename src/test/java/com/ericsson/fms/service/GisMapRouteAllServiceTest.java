package com.ericsson.fms.service;

import com.ericsson.fms.entity.RouteMessage;
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
public class GisMapRouteAllServiceTest {
    @MockBean
    private GisMapRouteAllService gisMapRouteAllService;

    @Test
    public void testGetRoute() throws Exception{
        RouteMessage routeMessage = new RouteMessage();
        Mockito.when(gisMapRouteAllService.getRoute(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(routeMessage);
    }
}
