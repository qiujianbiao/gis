package com.ericsson.fms.service;

import com.ericsson.fms.entity.Distancematrix;
import com.ericsson.fms.entity.QueryDistancematrixBody;
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
public class GisMapDistancematrixServiceTest {
    @MockBean
    private GisMapDistancematrixService gisMapDistancematrixService;

    @Test
    public void testGetDistancematrix() throws Exception{
        Distancematrix distancematrix = new Distancematrix();
        QueryDistancematrixBody body = new QueryDistancematrixBody();
        Mockito.when(gisMapDistancematrixService.getDistancematrix(body)).thenReturn(distancematrix);
    }
}
