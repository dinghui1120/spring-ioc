package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

public class DhAspectJAfterThrowingAdvice extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private String throwName;

    public DhAspectJAfterThrowingAdvice(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable ex) {
            invokeAdviceMethod(mi, null, ex);
            throw ex;
        }
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
