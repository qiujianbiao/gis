package com.ericsson.fms.dao;

import com.ericsson.fms.domain.*;
import com.ericsson.fms.entity.*;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GisRoadDao{
	public List<RoadBean> getRoadInfo(LocationBean lbean);
	public List<RoadBean> getPlaceInfo(LocationBean lbean);

	public List<RoadHistory> getRoadHistorys(RoadHistoryRequest requset);
	public void removeDuplicateRoadHistory();

	public void insertRoadSegmentsByBatch(List<RoadSegment> roadSegmentList);
	public void deleteRoadSegmentsBySegId(List<RoadSegment> roadSegmentList);

	public List<Salik> getSalik();

	public RoadSegment getGisRoadSingle(LocationBean lbean);
	public void updateGisRoad(RoadSegment roadSegment);


	public Page<RoadSegment> findPageRoadSegment(RoadSegmentRequest rq);

	public Page<RoadSegment> findPageAllRoadSegment();

	public List<RoadTraffic> getRoadTraffic(RoadTrafficRequest request);
	public List<RoadTraffic> getRoadTrafficHistory(RoadTrafficRequest request);

	public void insertRoadHistoryList(List<RoadHistory> roadHistoryList);


}
