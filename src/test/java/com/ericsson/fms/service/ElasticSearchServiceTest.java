package com.ericsson.fms.service;

import com.ericsson.fms.entity.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejioqiu on 12/20/2017.
 */
@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ElasticSearchServiceTest {
    @MockBean
    private ElasticSearchService elasticSearchService;

    @Test
    public void testGetGisHeatmap() throws Exception{
        List<Location> list = new ArrayList<Location>();
        Mockito.when(elasticSearchService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(list);
    }
}
