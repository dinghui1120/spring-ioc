package com.dh.framework.aop.config;

import lombok.Data;

/**
 * AOP配置类
 */
@Data
public class DhAopConfig {

    /**
     * 切面表达式
     */
    private String pointCut;

    /**
     * 切面类
     */
    private String aspectClass;

    /**
     * 前置通知方法名
     */
    private String aspectBefore;

    /**
     * 返回通知方法名
     */
    private String aspectAfterReturn;

    /**
     * 异常通知方法名
     */
    private String aspectAfterThrow;

    /**
     * 异常通知中使用的异常参数名
     */
    private String aspectAfterThrowingName;

}
