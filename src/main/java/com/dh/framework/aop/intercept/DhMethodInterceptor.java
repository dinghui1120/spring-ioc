package com.dh.framework.aop.intercept;

public interface DhMethodInterceptor {

    Object invoke(DhMethodInvocation invocation) throws Throwable;

}
