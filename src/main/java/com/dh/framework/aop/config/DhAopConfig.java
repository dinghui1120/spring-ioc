package com.dh.framework.aop.config;

import lombok.Data;

/**
 * AOP配置类
 */
@Data
public class DhAopConfig {

    /**
     * 切面类
     */
    private String aspectClass;

    /**
     * 切面表达式
     */
    private String pointCut;

    /**
     * 前置通知方法名
     */
    private String aspectBefore;

    /**
     * 后置通知方法名
     */
    private String aspectAfter;

    /**
     * 返回通知方法名
     */
    private String aspectAfterReturn;

    /**
     * 异常通知方法名
     */
    private String aspectAfterThrow;

    /**
     * 环绕通知方法名
     */
    private String aspectAround;

    /**
     * 异常通知中要捕获的异常类型全限定名
     * 如果不指定，则捕获所有Throwable异常
     */
    private String aspectAfterThrowingType;

}
