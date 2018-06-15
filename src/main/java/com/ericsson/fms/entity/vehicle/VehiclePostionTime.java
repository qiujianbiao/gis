package com.ericsson.fms.entity.vehicle;

import com.ericsson.fms.entity.Position;

/**
 * Created by ejioqiu on 3/5/2018.
 */
public class VehiclePostionTime {
    private String time;
    private Position location;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Position getLocation() {
        return location;
    }

    public void setLocation(Position location) {
        this.location = location;
    }
}
