package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/14/2017.
 */
public class Bounds {
    private Position northeast;
    private Position southwest;

    public Position getNortheast() {
        return northeast;
    }

    public void setNortheast(Position northeast) {
        this.northeast = northeast;
    }

    public Position getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Position southwest) {
        this.southwest = southwest;
    }
}
