package com.dh.framework.aop.aspect.interceptor;


import com.dh.framework.aop.aspect.advice.DhAbstractAspectJAdvice;
import com.dh.framework.aop.aspect.joinpoint.DhJoinPoint;
import com.dh.framework.aop.aspect.joinpoint.DhSimpleJoinPoint;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 异常通知拦截器
 * 在目标方法抛出异常时执行
 */
@Slf4j
public class DhAspectJAfterThrowingInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    /**
     * 要捕获的异常类型
     */
    private Class<?> throwType;

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
            // 处理InvocationTargetException，获取原始异常
            Throwable targetEx = unwrapThrowable(ex);
            log.info("configThrowType:{}, exType:{}", throwType, targetEx.getClass());
            // 如果指定了异常类型，则只处理该类型的异常
            if (throwType == null || throwType.isAssignableFrom(targetEx.getClass())) {
                DhJoinPoint jp = new DhSimpleJoinPoint(mi.getThis(), mi.getMethod(), mi.getArguments(), mi.getTargetClass());
                invokeAdviceMethod(jp, null, targetEx);
            }
            throw targetEx;
        }
    }
    
    /**
     * 获取原始异常
     * 如果是InvocationTargetException，则获取其目标异常
     */
    private Throwable unwrapThrowable(Throwable ex) {
        if (ex instanceof InvocationTargetException) {
            return ((InvocationTargetException) ex).getTargetException();
        }
        return ex;
    }
    
    /**
     * 设置要捕获的异常类型名称
     * 此方法用于指定拦截器只处理特定类型的异常
     * @param typeName 异常类型的全限定名
     */
    public void setThrowType(String typeName) {
        try {
            throwType = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            log.error("异常类型[{}]不存在", typeName, e);
        }
    }
    
    /**
     * 获取要捕获的异常类型
     */
    public Class<?> getThrowType() {
        return throwType;
    }
}
