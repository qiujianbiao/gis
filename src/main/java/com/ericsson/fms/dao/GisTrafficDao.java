package com.ericsson.fms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ericsson.fms.domain.GisTrafficVo;

@Mapper
public interface GisTrafficDao{
	
	public int alterTrafficTmpName();
    
    public int dropTrafficTable();
    
    public int creatTrafficTmpTable();
    
    public int updateTrafficSpeedBucket();
    
	public int insertTrafficTmpByBatch(List<GisTrafficVo> trafficList);
	
	public int insertTrafficTmp(GisTrafficVo vo);
		
	public int insertTrafficHistory();
	
	public int delTrafficHistory(@Param("keepdays") int keepdays);
}
