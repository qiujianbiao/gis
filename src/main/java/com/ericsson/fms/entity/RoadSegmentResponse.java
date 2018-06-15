package com.ericsson.fms.entity;

import com.ericsson.fms.domain.RoadSegment;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * Created by ejioqiu on 1/23/2018.
 */
public class RoadSegmentResponse {
    private Integer startIndex;
    private Integer totalResult;
    private Integer itemsPerPage;
    private List<RoadSegment> roads;

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(Integer totalResult) {
        this.totalResult = totalResult;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<RoadSegment> getRoads() {
        return roads;
    }

    public void setRoads(List<RoadSegment> roads) {
        this.roads = roads;
    }
}
