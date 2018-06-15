package com.ericsson.fms.entity;

import java.io.Serializable;

public class Enterprise implements Serializable{
	private static final long serialVersionUID = -526324944915280489L;
	private String id;
	public static final String OBJECT_KEY = "GIS_ENTERPRISE_1";
	
	private String vehicleType;
	
	private String[] driverIds;
	
	private String enterpriseType;
	
	private String enterpriseId;

	private String oemId;

	private String fleetId;
	
	private String enterpriseName;

	private String driverLicenseNumber;
	private String driverUserName;

	public Enterprise(String id) {  
        this.id = id;  
    }
	
	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String[] getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(String[] driverIds) {
		this.driverIds = driverIds;
	}

	public String getEnterpriseType() {
		return enterpriseType;
	}

	public void setEnterpriseType(String enterpriseType) {
		this.enterpriseType = enterpriseType;
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
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static String getObjectKey() {
		return OBJECT_KEY;
	}

	public String getOemId() {
		return oemId;
	}

	public void setOemId(String oemId) {
		this.oemId = oemId;
	}

	public String getFleetId() {
		return fleetId;
	}

	public void setFleetId(String fleetId) {
		this.fleetId = fleetId;
	}

	public String getDriverLicenseNumber() {
		return driverLicenseNumber;
	}

	public void setDriverLicenseNumber(String driverLicenseNumber) {
		this.driverLicenseNumber = driverLicenseNumber;
	}

	public String getDriverUserName() {
		return driverUserName;
	}

	public void setDriverUserName(String driverUserName) {
		this.driverUserName = driverUserName;
	}
}
