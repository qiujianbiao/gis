package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 1/11/2018.
 */
public class QueryDistancematrixBody {
    private Matrix[] matrix;
    private String destination;

    private String departureTime;
    private String mode;
    private String language;

    public Matrix[] getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix[] matrix) {
        this.matrix = matrix;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
