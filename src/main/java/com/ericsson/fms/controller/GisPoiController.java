package com.ericsson.fms.controller;

import com.ericsson.fms.domain.GisPoiVo;
import com.ericsson.fms.domain.GisTrafficVo;
import com.ericsson.fms.domain.PoiBean;
import com.ericsson.fms.entity.BatchcreateResult;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisPoiService;
import com.ericsson.fms.service.GisTrafficService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/gis")
public class GisPoiController extends BaseController{
	@Autowired
	private GisPoiService gisPoiService;

	@RequestMapping(value = "/maps/v1/rpc/search-poi", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public List<PoiBean> getPoiInfo(@RequestBody Map<String, Object> queryMap) throws HttpException{
		List<PoiBean> list = new ArrayList<PoiBean>();
		logger.info("=======getPoiInfo=======");
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			logger.info("gis-service getPoiInfo request:"+mapper.writeValueAsString(queryMap));
			list = gisPoiService.getPoiInfo(queryMap);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service getPoiInfo time:"+(endtime-starttime)+"ms");
			logger.info("gis-service getPoiInfo result:"+mapper.writeValueAsString(list));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return list;
	}

	@RequestMapping(value = "/maps/v1/rpc/poi", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public BatchcreateResult insertPoiByBatch(@RequestBody List<GisPoiVo> list) throws HttpException{
		logger.info("=======insertPoiByBatch=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisPoiService.insertPoiByBatch(list);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service insertPoiByBatch time:"+(endtime-starttime)+"ms");
			logger.info("gis-service insertPoiByBatch result:"+mapper.writeValueAsString(br));
			return br;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
	}

	@RequestMapping(value = "/maps/v1/rpc/poi", method = RequestMethod.PUT)
	public BatchcreateResult updPoi(@RequestBody GisPoiVo vo) throws HttpException {
		logger.info("=======updPoi=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisPoiService.updPoi(vo);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service updPoi time:"+(endtime-starttime)+"ms");
			logger.info("gis-service updPoi result:"+mapper.writeValueAsString(br));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return br;
	}


	@RequestMapping(value = "/maps/v1/rpc/poi", method = RequestMethod.DELETE)
	public BatchcreateResult delPoi(String ids) throws HttpException {
		logger.info("=======delPoi=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisPoiService.delPoi(ids);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service delPoi time:"+(endtime-starttime)+"ms");
			logger.info("gis-service delPoi result:"+mapper.writeValueAsString(br));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return br;
	}

}
