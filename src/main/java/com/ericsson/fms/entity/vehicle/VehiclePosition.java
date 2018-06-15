package com.ericsson.fms.entity.vehicle;

import java.util.List;

/**
 * Created by ejioqiu on 3/5/2018.
 */
public class VehiclePosition {
    private String vin;
    private String driverName;
    private String driverLicenseNum;

    private VehiclePostionTime first;
    private VehiclePostionTime last;

    private List<VehiclePostionTime> position;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLicenseNum() {
        return driverLicenseNum;
    }

    public void setDriverLicenseNum(String driverLicenseNum) {
        this.driverLicenseNum = driverLicenseNum;
    }

    public VehiclePostionTime getFirst() {
        return first;
    }

    public void setFirst(VehiclePostionTime first) {
        this.first = first;
    }

    public VehiclePostionTime getLast() {
        return last;
    }

    public void setLast(VehiclePostionTime last) {
        this.last = last;
    }

    public List<VehiclePostionTime> getPosition() {
        return position;
    }

    public void setPosition(List<VehiclePostionTime> position) {
        this.position = position;
    }
}
