package com.ericsson.fms.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ericsson.fms.dao.GisTrafficDao;
import com.ericsson.fms.domain.GisTrafficVo;
import com.ericsson.fms.entity.BatchcreateResult;
import com.ericsson.fms.exception.http.HttpException;
import com.ericsson.fms.exception.http.HttpInternalServerError;

/**
 * Created by ejioqiu on 4/26/2018.
 */

@Service
public class GisTrafficService {
    public static final Logger logger = LoggerFactory.getLogger(GisTrafficService.class);

    @Resource
    private GisTrafficDao gisTrafficDao;

    public BatchcreateResult creatTrafficTmpTable() throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisTrafficDao.creatTrafficTmpTable();
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("creatTrafficTmpTable error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult insertTrafficTmpByBatch(List<GisTrafficVo> trafficList) throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisTrafficDao.insertTrafficTmpByBatch(trafficList);
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("insertTrafficTmpByBatch error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult controlTraffic()throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            gisTrafficDao.dropTrafficTable();
            gisTrafficDao.alterTrafficTmpName();
            result = gisTrafficDao.updateTrafficSpeedBucket();
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("controlTraffic error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult insertTrafficHistory() throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisTrafficDao.insertTrafficHistory();
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("insertTrafficHistory error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

    public BatchcreateResult delTrafficHistory(Integer keepDays) throws HttpException {
        int result = -1;
        BatchcreateResult br = new BatchcreateResult();
        try {
            result = gisTrafficDao.delTrafficHistory(keepDays);
            br.setSuccessfulItems(String.valueOf(result));
        }  catch (Exception e) {
        	logger.error("delTrafficHistory error",e);
            throw new HttpInternalServerError("Internal server error");
        }
        return br;
    }

}
