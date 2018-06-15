package com.ericsson.fms.domain;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by ejioqiu on 1/15/2018.
 */
public class RoadSegment {

    private String segId;
    private Integer gid;
    private BigInteger previousSegId;
    private BigInteger nextSegId;
    private Integer frc;
    private String distance;
    private Integer lanes;//nlan
    private Double startLat;
    private Double startLong;
    private Double endLat;
    private Double endLong;
    private String roadNumber;
    private String roadName;
    private String roadDirection;
    private String roadList;
    private String country;
    private String state;
    private String county;
    private String city;
    private Integer slipRoad;
    private String bearing;
    private Integer speedLimit;
    private String coordinates;
    private String lastModified;

    private Float objectId;
    private Float strt;
    private Float fsnd;
    private Float head;
    private Integer refe;
    private Integer hier;
    private String salikName;

    public String getSegId() {
        return segId;
    }

    public void setSegId(String segId) {
        this.segId = segId;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public BigInteger getPreviousSegId() {
        return previousSegId;
    }

    public void setPreviousSegId(BigInteger previousSegId) {
        this.previousSegId = previousSegId;
    }

    public BigInteger getNextSegId() {
        return nextSegId;
    }

    public void setNextSegId(BigInteger nextSegId) {
        this.nextSegId = nextSegId;
    }

    public Integer getFrc() {
        return frc;
    }

    public void setFrc(Integer frc) {
        this.frc = frc;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getLanes() {
        return lanes;
    }

    public void setLanes(Integer lanes) {
        this.lanes = lanes;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLong() {
        return startLong;
    }

    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLong() {
        return endLong;
    }

    public void setEndLong(Double endLong) {
        this.endLong = endLong;
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

    public String getRoadDirection() {
        return roadDirection;
    }

    public void setRoadDirection(String roadDirection) {
        this.roadDirection = roadDirection;
    }

    public String getRoadList() {
        return roadList;
    }

    public void setRoadList(String roadList) {
        this.roadList = roadList;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getSlipRoad() {
        return slipRoad;
    }

    public void setSlipRoad(Integer slipRoad) {
        this.slipRoad = slipRoad;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Float getObjectId() {
        return objectId;
    }

    public void setObjectId(Float objectId) {
        this.objectId = objectId;
    }

    public Float getStrt() {
        return strt;
    }

    public void setStrt(Float strt) {
        this.strt = strt;
    }

    public Float getFsnd() {
        return fsnd;
    }

    public void setFsnd(Float fsnd) {
        this.fsnd = fsnd;
    }

    public Float getHead() {
        return head;
    }

    public void setHead(Float head) {
        this.head = head;
    }

    public Integer getRefe() {
        return refe;
    }

    public void setRefe(Integer refe) {
        this.refe = refe;
    }

    public Integer getHier() {
        return hier;
    }

    public void setHier(Integer hier) {
        this.hier = hier;
    }

    public String getSalikName() {
        return salikName;
    }

    public void setSalikName(String salikName) {
        this.salikName = salikName;
    }
}
