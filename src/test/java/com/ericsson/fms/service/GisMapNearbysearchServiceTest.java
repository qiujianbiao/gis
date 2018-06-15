package com.ericsson.fms.service;

import com.ericsson.fms.entity.Nearbysearch;
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
public class GisMapNearbysearchServiceTest {
    @MockBean
    private GisMapNearbysearchService gisMapNearbysearchService;

    @Test
    public void testGetNearbysearch() throws Exception{
        Nearbysearch nearbysearch = new Nearbysearch();
        Mockito.when(gisMapNearbysearchService.getNearbysearch(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(nearbysearch);
    }
}
