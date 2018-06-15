package com.ericsson.fms.controller;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.fms.domain.GisTrafficVo;
import com.ericsson.fms.entity.BatchcreateResult;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;
import com.ericsson.fms.service.GisTrafficService;

@RestController
@RequestMapping(value="/gis")
public class GisTrafficController extends BaseController{
	@Autowired
	private GisTrafficService gisTrafficService;

	@RequestMapping(value = "/road/v1/create-traffic-tmp", method = RequestMethod.PUT)
	public BatchcreateResult creatTrafficTmpTable() throws HttpException{
		logger.info("=======creatTrafficTmpTable=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisTrafficService.creatTrafficTmpTable();
			long endtime = System.currentTimeMillis();
			logger.info("gis-service creatTrafficTmpTable time:"+(endtime-starttime)+"ms");
			logger.info("gis-service creatTrafficTmpTable result:"+mapper.writeValueAsString(br));
			return br;
		} catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpInternalServerError("Internal server error");
        }
	}


	@RequestMapping(value = "/road/v1/traffic", method = RequestMethod.POST, consumes = "application/json", produces = "application/json;charset=UTF-8")
	public BatchcreateResult insertTrafficTmpByBatch(@RequestBody List<GisTrafficVo> trafficList) throws HttpException{
		logger.info("=======insertTrafficTmpByBatch=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisTrafficService.insertTrafficTmpByBatch(trafficList);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service insertTrafficTmpByBatch time:"+(endtime-starttime)+"ms");
			logger.info("gis-service insertTrafficTmpByBatch result:"+mapper.writeValueAsString(br));
			return br;
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
	}

	@RequestMapping(value = "/road/v1/traffic-control", method = RequestMethod.PUT)
	public BatchcreateResult controlTraffic() throws HttpException {
		logger.info("=======controlTraffic=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisTrafficService.controlTraffic();
			long endtime = System.currentTimeMillis();
			logger.info("gis-service controlTraffic time:"+(endtime-starttime)+"ms");
			logger.info("gis-service controlTraffic result:"+mapper.writeValueAsString(br));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return br;
	}

	@RequestMapping(value = "/road/v1/traffic-history", method = RequestMethod.POST)
	public BatchcreateResult insertTrafficHistory() throws HttpException {
		logger.info("=======insertTrafficHistory=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			br = gisTrafficService.insertTrafficHistory();
			long endtime = System.currentTimeMillis();
			logger.info("gis-service insertTrafficHistory time:"+(endtime-starttime)+"ms");
			logger.info("gis-service insertTrafficHistory result:"+mapper.writeValueAsString(br));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return br;
	}


	@RequestMapping(value = "/road/v1/traffic-history", method = RequestMethod.DELETE)
	public BatchcreateResult delTrafficHistory(Integer keepDays) throws HttpException {
		logger.info("=======delTrafficHistory=======");
		BatchcreateResult br = new BatchcreateResult();
		long starttime = System.currentTimeMillis();
		ObjectMapper mapper = new ObjectMapper();
		try {
			keepDays = keepDays == null ? 7 : keepDays;
			logger.info("gis-service delTrafficHistory keepDays:"+keepDays);
			br = gisTrafficService.delTrafficHistory(keepDays);
			long endtime = System.currentTimeMillis();
			logger.info("gis-service delTrafficHistory time:"+(endtime-starttime)+"ms");
			logger.info("gis-service delTrafficHistory result:"+mapper.writeValueAsString(br));
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpInternalServerError("Internal server error");
		}
		return br;
	}

}
