package com.dh.framework.aop.aspect.joinpoint;

import java.lang.reflect.Method;

/**
 * 连接点
 * 定义方法执行过程中的相关信息
 */
public interface DhJoinPoint {

    /**
     * 获取目标对象
     */
    Object getTarget();

    /**
     * 获取方法参数
     */
    Object[] getArguments();

    /**
     * 获取目标方法
     */
    Method getMethod();

    /**
     * 设置自定义属性
     */
    void setUserAttribute(String key, Object value);

    /**
     * 获取自定义属性
     */
    Object getUserAttribute(String key);

}
