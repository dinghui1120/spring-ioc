package com.dh.framework.aop.aspect.interceptor;


import com.dh.framework.aop.aspect.advice.DhAbstractAspectJAdvice;
import com.dh.framework.aop.aspect.joinpoint.DhJoinPoint;
import com.dh.framework.aop.aspect.joinpoint.DhSimpleJoinPoint;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * 前置通知拦截器
 * 在目标方法执行之前执行
 */
public class DhMethodBeforeAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;

    public DhMethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 先执行前置通知，再执行目标方法
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        jp = new DhSimpleJoinPoint(mi.getTarget(), mi.getMethod(), mi.getArguments(), mi.getTargetClass());
        before();
        return mi.proceed();
    }

    public void before() throws Throwable{
        invokeAdviceMethod(jp,null,null);
    }

}
