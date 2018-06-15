package com.ericsson.fms.entity;

public class Location {
	private Double lat;
	private Double lon;
	private Long count;
	
	public Location() {
	}
	
	public Location(Double lat, Double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
}
