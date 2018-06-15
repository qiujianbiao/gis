package com.ericsson.fms.entity;

import java.util.List;

/**
 * Created by ejioqiu on 3/7/2018.
 */
public class VehiclesResponse {
    private List<EsBean> vehicles;
    public List<EsBean> getVehicles() {
        return vehicles;
    }
    public void setVehicles(List<EsBean> vehicles) {
        this.vehicles = vehicles;
    }
}
