package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/14/2017.
 */
public class Steps {
    private DistanceAndDuration distance;
    private DistanceAndDuration duration;
    private Position startLocation;
    private Position endLocation;
    private Polyline polyline;
    private String htmlInstructions;
    private String travelMode;

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

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }
}
