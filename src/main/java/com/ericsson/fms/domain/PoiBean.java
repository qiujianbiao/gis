package com.ericsson.fms.domain;

/**
 * Created by ejioqiu on 4/26/2018.
 */
public class PoiBean {
    private Integer id;
    private String label;
    private String description;
    private String address;
    private String geomType;

    private String pointCoordinates;
    private String lineCoordinates;
    private String polygonCoordinates;
    private String circleCoordinates;

    private Float circleRadius;
    private String category;
    private String tags;
    private String links;
    private String author;
    private String externalId;
    private String oemId;
    private String enterpriseIds;
    private String displayMode;
    private String status;
    private String createdTime;
    private String updatedTime;
    private String deletedTime;
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

    public String getPointCoordinates() {
        return pointCoordinates;
    }

    public void setPointCoordinates(String pointCoordinates) {
        this.pointCoordinates = pointCoordinates;
    }

    public String getLineCoordinates() {
        return lineCoordinates;
    }

    public void setLineCoordinates(String lineCoordinates) {
        this.lineCoordinates = lineCoordinates;
    }

    public String getPolygonCoordinates() {
        return polygonCoordinates;
    }

    public void setPolygonCoordinates(String polygonCoordinates) {
        this.polygonCoordinates = polygonCoordinates;
    }

    public String getCircleCoordinates() {
        return circleCoordinates;
    }

    public void setCircleCoordinates(String circleCoordinates) {
        this.circleCoordinates = circleCoordinates;
    }

    public Float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(Float circleRadius) {
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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(String deletedTime) {
        this.deletedTime = deletedTime;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
