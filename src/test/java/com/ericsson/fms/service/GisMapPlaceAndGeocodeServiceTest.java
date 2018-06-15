package com.ericsson.fms.service;

import com.ericsson.fms.entity.CityInfo;
import com.ericsson.fms.entity.GeoCodeInfo;
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
public class GisMapPlaceAndGeocodeServiceTest {
    @MockBean
    private GisMapPlaceAndGeocodeService gisMapPlaceAndGeocodeService;

    @Test
    public void testGetCity() throws Exception{
        CityInfo cityInfo = new CityInfo();
        Mockito.when(gisMapPlaceAndGeocodeService.getCity(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyString())).thenReturn(cityInfo);
    }

    @Test
    public void testGetGeoCodeInfo() throws Exception{
        GeoCodeInfo geoCodeInfo = new GeoCodeInfo();
        Mockito.when(gisMapPlaceAndGeocodeService.getGeoCodeInfo(Mockito.anyString(), Mockito.anyString())).thenReturn(geoCodeInfo);
    }
}
