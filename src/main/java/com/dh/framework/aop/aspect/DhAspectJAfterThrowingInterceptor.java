package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * 异常通知拦截器
 * 在目标方法抛出异常时执行
 */
public class DhAspectJAfterThrowingInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    /**
     * 异常参数名
     */
    private String throwName;

    public DhAspectJAfterThrowingInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 抛出异常时执行通知方法
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(mi, null, ex);
            throw ex;
        }
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }

}
