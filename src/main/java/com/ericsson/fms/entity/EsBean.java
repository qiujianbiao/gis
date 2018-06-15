package com.ericsson.fms.entity;

/**
 * Created by ejioqiu on 3/7/2018.
 */
public class EsBean {
    private String oemId;
    private String vin;
    private String enterpriseId;
    private String enterpriseName;
    private String fleetId;
    private String vehicleType;
    private String timestamp;
    private Position position;
    private String driverName;
    private String driverLicenseNum;

    private String id;

    public EsBean() {}

    public EsBean(String oemId, String vin, String enterpriseId, String enterpriseName, String fleetId, String vehicleType, String timestamp, Position position,String driverName,String driverLicenseNum,String id) {
        this.oemId = oemId;
        this.vin = vin;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.fleetId = fleetId;
        this.vehicleType = vehicleType;
        this.timestamp = timestamp;
        this.position = position;
        this.driverName = driverName;
        this.driverLicenseNum = driverLicenseNum;
        this.id = id;
    }

    public String getOemId() {
        return oemId;
    }

    public void setOemId(String oemId) {
        this.oemId = oemId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getFleetId() {
        return fleetId;
    }

    public void setFleetId(String fleetId) {
        this.fleetId = fleetId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
