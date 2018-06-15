package com.ericsson.fms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by ewwuyyu on 2017/7/19.
 */
public abstract class BaseController {

    public static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    public Environment ev;

}
