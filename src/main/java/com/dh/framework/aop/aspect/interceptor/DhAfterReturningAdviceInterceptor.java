package com.dh.framework.aop.aspect.interceptor;


import com.dh.framework.aop.aspect.advice.DhAbstractAspectJAdvice;
import com.dh.framework.aop.aspect.joinpoint.DhJoinPoint;
import com.dh.framework.aop.aspect.joinpoint.DhSimpleJoinPoint;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * 返回值通知拦截器
 * 方法成功执行后执行
 */
public class DhAfterReturningAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;

    public DhAfterReturningAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 先执行目标方法，再执行返回通知
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        jp = new DhSimpleJoinPoint(mi.getThis(), mi.getMethod(), mi.getArguments(), mi.getTargetClass());
        Object retVal = mi.proceed();
        afterReturning(retVal);
        return retVal;
    }

    private void afterReturning(Object returnValue) throws Throwable{
        invokeAdviceMethod(jp, returnValue, null);
    }

}
