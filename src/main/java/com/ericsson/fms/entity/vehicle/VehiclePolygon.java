package com.ericsson.fms.entity.vehicle;

import java.util.List;

/**
 * Created by ejioqiu on 3/5/2018.
 */
public class VehiclePolygon {
    private List<VehiclePosition> vehicles;

    private Integer pageNo;//当前页
    private Integer pageSize;//每页显示记录条数
    private Integer totalPage;//总页数
    private Integer star;//开始数据
    private Integer total;//总记录数

    public List<VehiclePosition> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehiclePosition> vehicles) {
        this.vehicles = vehicles;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
