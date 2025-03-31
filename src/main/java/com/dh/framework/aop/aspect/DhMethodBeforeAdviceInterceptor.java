package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

public class DhMethodBeforeAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;

    public DhMethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    public void before(Method method, Object[] arguments, Object aThis) throws Throwable{
        invokeAdviceMethod(jp,null,null);
    }

    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        jp = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

}
