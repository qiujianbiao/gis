package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 11/9/2017.
 */
public class GeoCodeInfo {
    private String formattedAddress;
    private String source;
    private Position position;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
