package com.ericsson.fms.entity;

import java.io.Serializable;

/**
 * Created by ejioqiu on 11/9/2017.
 */
public class CityInfo implements Serializable {
    private static final long serialVersionUID = 1353753404478261260L;
    private String formattedAddress;
    private String cityName;
    private String source;
    private String[] types;
    private Position position;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
