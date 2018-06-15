package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/14/2017.
 */
public class RouteMessage {
    private Bounds bounds;
    private Legs[] legs;
    private Polyline overviewPolyline;
    private String summary;
    private String source;

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Legs[] getLegs() {
        return legs;
    }

    public void setLegs(Legs[] legs) {
        this.legs = legs;
    }

    public Polyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(Polyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
