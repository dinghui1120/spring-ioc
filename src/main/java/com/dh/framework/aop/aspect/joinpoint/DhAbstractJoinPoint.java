package com.dh.framework.aop.aspect.joinpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * DhJoinPoint接口的抽象实现
 * 提供所有连接点实现的通用基础功能
 */
public abstract class DhAbstractJoinPoint implements DhJoinPoint {

    /**
     * 目标对象
     */
    protected final Object target;

    /**
     * 目标方法
     */
    protected final Method method;

    /**
     * 方法参数
     */
    protected Object[] arguments;

    /**
     * 目标类
     */
    protected final Class<?> targetClass;

    /**
     * 用户自定义属性
     */
    protected Map<String, Object> userAttributes = new HashMap<>();

    public DhAbstractJoinPoint(Object target, Method method, Object[] arguments, Class<?> targetClass) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        userAttributes.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return userAttributes.get(key);
    }

    /**
     * 获取目标类
     */
    public Class<?> getTargetClass() {
        return this.targetClass;
    }
} 