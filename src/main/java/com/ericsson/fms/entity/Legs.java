package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/14/2017.
 */
public class Legs {
    private DistanceAndDuration distance;
    private DistanceAndDuration duration;
    private Position startLocation;
    private Position endLocation;
    private String startAddress;
    private String endAddress;
    private Steps[] steps;
    private String[] trafficSpeedEntry;
    private String[] viaWaypoint;

    public DistanceAndDuration getDistance() {
        return distance;
    }

    public void setDistance(DistanceAndDuration distance) {
        this.distance = distance;
    }

    public DistanceAndDuration getDuration() {
        return duration;
    }

    public void setDuration(DistanceAndDuration duration) {
        this.duration = duration;
    }

    public Position getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Position startLocation) {
        this.startLocation = startLocation;
    }

    public Position getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Position endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Steps[] getSteps() {
        return steps;
    }

    public void setSteps(Steps[] steps) {
        this.steps = steps;
    }

    public String[] getTrafficSpeedEntry() {
        return trafficSpeedEntry;
    }

    public void setTrafficSpeedEntry(String[] trafficSpeedEntry) {
        this.trafficSpeedEntry = trafficSpeedEntry;
    }

    public String[] getViaWaypoint() {
        return viaWaypoint;
    }

    public void setViaWaypoint(String[] viaWaypoint) {
        this.viaWaypoint = viaWaypoint;
    }
}
