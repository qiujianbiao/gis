package com.ericsson.fms.domain;

public class GisPoiVo {
	
	private Integer id;
	private String label;
    private String description;
    private String address;
    private String geomType;
    private String geomPoint;
    private String geomLine;
    private String geomPolygon;
    private String geomCircle;
    private Double circleRadius;
    private String category;
    private String tags;
    private String links;
    private String author;
    private String externalId;
    private String oemId;
    private String enterpriseIds;
    private String displayMode;
    private String status;
    private String metadata;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGeomType() {
		return geomType;
	}
	public void setGeomType(String geomType) {
		this.geomType = geomType;
	}
	public String getGeomPoint() {
		return geomPoint;
	}
	public void setGeomPoint(String geomPoint) {
		this.geomPoint = geomPoint;
	}
	public String getGeomLine() {
		return geomLine;
	}
	public void setGeomLine(String geomLine) {
		this.geomLine = geomLine;
	}
	public String getGeomPolygon() {
		return geomPolygon;
	}
	public void setGeomPolygon(String geomPolygon) {
		this.geomPolygon = geomPolygon;
	}
	public String getGeomCircle() {
		return geomCircle;
	}
	public void setGeomCircle(String geomCircle) {
		this.geomCircle = geomCircle;
	}
	public Double getCircleRadius() {
		return circleRadius;
	}
	public void setCircleRadius(Double circleRadius) {
		this.circleRadius = circleRadius;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getLinks() {
		return links;
	}
	public void setLinks(String links) {
		this.links = links;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getOemId() {
		return oemId;
	}
	public void setOemId(String oemId) {
		this.oemId = oemId;
	}
	public String getEnterpriseIds() {
		return enterpriseIds;
	}
	public void setEnterpriseIds(String enterpriseIds) {
		this.enterpriseIds = enterpriseIds;
	}
	public String getDisplayMode() {
		return displayMode;
	}
	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
