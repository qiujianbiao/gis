package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 1/29/2018.
 */
public class RoadTrafficRequest {
    private double lat;
    private double lon;
    private String startTime;
    private String endTime;
    private Integer fore;
    private double distance;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getFore() {
        return fore;
    }

    public void setFore(Integer fore) {
        this.fore = fore;
    }
}
