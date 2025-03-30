package com.dh.demo.aspect;

import com.dh.framework.aop.aspect.DhJoinPoint;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LogAspect {

    public void before(DhJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoke Before Method");
    }

    public void after(DhJoinPoint joinPoint){
        long startTime = (Long)joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("Invoke After Method" + "use time :" + (endTime - startTime));
    }

    public void afterThrowing(DhJoinPoint joinPoint,Throwable throwable){
        log.info("Invoke After Method,e" + throwable);
    }

}
