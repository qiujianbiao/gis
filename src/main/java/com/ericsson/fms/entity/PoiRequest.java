package com.ericsson.fms.entity;

import java.util.List;

public class PoiRequest {
	private Double lat;
	private Double lon;
	private String geomType;
	private String category;
	private String oemId;
	private String status;
	private String startTime;
	private String endTime;
	private String displayMode;
	private String[] enterpriseIds;
	private Float circleRadius;
	private List<Position> polygonList;
	private String polygonStr;

	private String label;
	private String address;
	private String[] tags;
	private String author;
	private String description;

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

	public String getGeomType() {
		return geomType;
	}

	public void setGeomType(String geomType) {
		this.geomType = geomType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOemId() {
		return oemId;
	}

	public void setOemId(String oemId) {
		this.oemId = oemId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}

	public String[] getEnterpriseIds() {
		return enterpriseIds;
	}

	public void setEnterpriseIds(String[] enterpriseIds) {
		this.enterpriseIds = enterpriseIds;
	}

	public Float getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(Float circleRadius) {
		this.circleRadius = circleRadius;
	}

	public List<Position> getPolygonList() {
		return polygonList;
	}

	public void setPolygonList(List<Position> polygonList) {
		this.polygonList = polygonList;
	}

	public String getPolygonStr() {
		return polygonStr;
	}

	public void setPolygonStr(String polygonStr) {
		this.polygonStr = polygonStr;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
