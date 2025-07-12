package com.dh.framework.aop.aspect.joinpoint;

import com.dh.framework.aop.intercept.DhMethodInvocation;

/**
 * DhProceedingJoinPoint的具体实现类
 * 用于环绕通知，提供执行目标方法的能力
 */
public class DhMethodInvocationProceedingJoinPoint extends DhAbstractJoinPoint implements DhProceedingJoinPoint {

    /**
     * 原始调用对象
     * 通过它来控制目标方法的执行
     */
    private final DhMethodInvocation methodInvocation;

    /**
     * 缓存参数，支持参数修改
     */
    private Object[] args;
    
    /**
     * 是否已经执行过proceed
     */
    private boolean proceeded = false;

    public DhMethodInvocationProceedingJoinPoint(DhMethodInvocation mi) {
        super(mi.getTarget(), mi.getMethod(), mi.getArguments(), mi.getTargetClass());
        this.methodInvocation = mi;
        this.args = mi.getArguments();
    }

    @Override
    public Object proceed() throws Throwable {
        proceeded = true;
        return methodInvocation.proceed();
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        if (args != null) {
            this.args = args;
        }
        proceeded = true;
        return methodInvocation.proceed(this.args);
    }

    /**
     * 重写获取参数方法，使用本地缓存的参数
     */
    @Override
    public Object[] getArguments() {
        return this.args;
    }
    
    /**
     * 获取原始MethodInvocation
     */
    public DhMethodInvocation getMethodInvocation() {
        return methodInvocation;
    }
    
    /**
     * 检查是否已经调用过proceed方法
     */
    public boolean hasProceeded() {
        return proceeded;
    }

} 