package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

public class DhAfterReturningAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;

    public DhAfterReturningAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }


    private void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable{
        this.invokeAdviceMethod(this.jp, returnValue, null);
    }

    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        jp = mi;
        // 调用到service的方法后，回到了afterReturning的invoke方法
        Object retVal = mi.proceed();
        afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

}
