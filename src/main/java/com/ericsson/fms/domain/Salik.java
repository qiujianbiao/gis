package com.ericsson.fms.domain;

/**
 * Created by ejioqiu on 4/19/2018.
 */
public class Salik {
    private String segId;
    private String salikName;
    private String roadName;
    private Double lat;
    private Double lon;

    public String getSalikName() {
        return salikName;
    }

    public void setSalikName(String salikName) {
        this.salikName = salikName;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getSegId() {
        return segId;
    }

    public void setSegId(String segId) {
        this.segId = segId;
    }
}
