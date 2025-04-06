package com.dh.framework.aop.aspect;

import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * 后置通知拦截器
 * 在目标方法执行完成后执行，无论方法是否抛出异常
 */
public class DhAfterAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;

    public DhAfterAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 先执行目标方法，再执行后置通知
     * 确保在任何情况下都会执行后置通知
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            jp = new DhSimpleJoinPoint(mi.getThis(), mi.getMethod(), mi.getArguments(), mi.getThis().getClass());
            return mi.proceed();
        } finally {
            after();
        }
    }

    public void after() throws Throwable {
        invokeAdviceMethod(jp, null, null);
    }

} 