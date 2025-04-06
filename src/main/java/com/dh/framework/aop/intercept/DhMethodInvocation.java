package com.dh.framework.aop.intercept;


import com.dh.framework.aop.aspect.DhAbstractJoinPoint;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法调用实现类
 * 封装了目标方法的调用，并维护了拦截器链
 */
public class DhMethodInvocation extends DhAbstractJoinPoint {

    /**
     * 代理对象
     */
    protected final Object proxy;

    /**
     * 拦截器链
     */
    protected final List<?> interceptorsAndDynamicMethodMatchers;

    /**
     * 当前执行到的拦截器索引
     */
    private int currentInterceptorIndex = -1;

    public DhMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
            super(target, method, arguments, targetClass);
            this.proxy = proxy;
            this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    /**
     * 执行方法调用
     */
    public Object proceed() throws Throwable {
        if (currentInterceptorIndex == interceptorsAndDynamicMethodMatchers.size() - 1) {
            // 执行service中的方法
            return method.invoke(target, arguments);
        }
        Object interceptorOrInterceptionAdvice = interceptorsAndDynamicMethodMatchers.get(++currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof DhMethodInterceptor) {
            DhMethodInterceptor mi = (DhMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }

    /**
     * 使用新的参数执行方法调用
     */
    public Object proceed(Object[] args) throws Throwable {
        if (args != null) {
            this.arguments = args;
        }
        return proceed();
    }
}
