package com.dh.demo.aspect;

import com.dh.framework.aop.aspect.DhJoinPoint;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LogAspect {

    public void before(DhJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        log.info("Invoke Before Method,methodName:{}", joinPoint.getMethod().getName());
    }

    public void after(DhJoinPoint joinPoint) {
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("Invoke After Method,methodName:{},cost time:{}", joinPoint.getMethod().getName(), (endTime - startTime) + "ms");
    }

    public void afterThrowing(DhJoinPoint joinPoint, Throwable ex) {
        log.info("Invoke After Throwing,methodName:{},e:{}", joinPoint.getMethod().getName(), ex);
    }

}
