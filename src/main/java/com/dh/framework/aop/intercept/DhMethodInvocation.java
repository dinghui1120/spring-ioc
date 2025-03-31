package com.dh.framework.aop.intercept;


import com.dh.framework.aop.aspect.DhJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DhMethodInvocation implements DhJoinPoint {

    protected final Object proxy;

    protected final Object target;

    protected final Method method;

    protected Object[] arguments;

    private final Class<?> targetClass;

    private Map<String, Object> userAttributes = new HashMap<>();

    protected final List<?> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    public DhMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
            this.proxy = proxy;
            this.target = target;
            this.targetClass = targetClass;
            this.method = method;
            this.arguments = arguments;
            // 每个方法对应的拦截链
            this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

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

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        userAttributes.put(key,value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return userAttributes.get(key);
    }

}
