package com.dh.demo.service;

import com.dh.framework.annotation.DhService;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试Cglib
 **/
@Slf4j
@DhService
public class TestCglibService {

    public String testNormal() {
        log.info("TestCglibService - testNormal");
        return "success";
    }

    public String testException() {
        log.info("TestCglibService - testException");
        int i = 1/0;
        return "1";
    }

}
