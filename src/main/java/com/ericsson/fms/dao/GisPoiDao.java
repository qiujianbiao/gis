package com.ericsson.fms.dao;

import com.ericsson.fms.domain.GisPoiVo;
import com.ericsson.fms.domain.PoiBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by ejioqiu on 4/26/2018.
 */
@Mapper
public interface GisPoiDao {
    public int insertPoiByBatch(List<GisPoiVo> list);
    public int updPoi(GisPoiVo vo);
    public int delPoi(@Param("ids") String ids);

    public List<PoiBean> getPoiInfo(Map<String, Object> queryMap);
}
