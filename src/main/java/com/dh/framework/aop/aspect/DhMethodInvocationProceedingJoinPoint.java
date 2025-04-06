package com.dh.framework.aop.aspect;

import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * DhProceedingJoinPoint的专用实现类
 * 封装了DhMethodInvocation，提供更丰富的连接点信息
 */
public class DhMethodInvocationProceedingJoinPoint implements DhProceedingJoinPoint {

    private final DhMethodInvocation methodInvocation;

    /**
     * 缓存参数，支持参数修改
     */
    private Object[] args;

    /**
     * 是否已经执行过proceed
     */
    private boolean proceeded = false;

    public DhMethodInvocationProceedingJoinPoint(DhMethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
        this.args = methodInvocation.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        proceeded = true;
        return methodInvocation.proceed(this.args);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        if (args != null) {
            this.args = args;
        }
        proceeded = true;
        return methodInvocation.proceed(this.args);
    }

    @Override
    public Object getThis() {
        return methodInvocation.getThis();
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public Method getMethod() {
        return methodInvocation.getMethod();
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        methodInvocation.setUserAttribute(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return methodInvocation.getUserAttribute(key);
    }
    
    /**
     * 获取目标类
     */
    public Class<?> getTargetClass() {
        return methodInvocation.getTargetClass();
    }
    
    /**
     * 检查是否已经调用过proceed方法
     */
    public boolean hasProceeded() {
        return proceeded;
    }
    
    /**
     * 获取原始MethodInvocation
     */
    public DhMethodInvocation getMethodInvocation() {
        return methodInvocation;
    }

} 