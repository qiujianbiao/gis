package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 1/11/2018.
 */
public class MatrixResponse {
    private Integer distance;
    private Integer duration;
    private String originAddresses;
    private String destinationAddress;

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(String originAddresses) {
        this.originAddresses = originAddresses;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
}
