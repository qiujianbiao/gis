package com.ericsson.fms.domain;

import java.math.BigInteger;

/**
 * Created by ejioqiu on 1/29/2018.
 */
public class RoadTraffic {
    private String segId;
    private String country;
    private String state;
    private String county;
    private String roadNumber;
    private String roadName;
    private String city;
    private Float speed;
    private Float baseSpeed;
    private String trafficTime;
    private Integer speedBucket;

    public String getSegId() {
        return segId;
    }

    public void setSegId(String segId) {
        this.segId = segId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public void setRoadNumber(String roadNumber) {
        this.roadNumber = roadNumber;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(Float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public Integer getSpeedBucket() {
        return speedBucket;
    }

    public void setSpeedBucket(Integer speedBucket) {
        this.speedBucket = speedBucket;
    }

    public String getTrafficTime() {
        return trafficTime;
    }

    public void setTrafficTime(String trafficTime) {
        this.trafficTime = trafficTime;
    }
}

