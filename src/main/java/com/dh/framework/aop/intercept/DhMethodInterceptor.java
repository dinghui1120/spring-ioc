package com.dh.framework.aop.intercept;

/**
 * 方法拦截器
 */
public interface DhMethodInterceptor {

    /**
     * 拦截方法调用
     */
    Object invoke(DhMethodInvocation invocation) throws Throwable;

}
