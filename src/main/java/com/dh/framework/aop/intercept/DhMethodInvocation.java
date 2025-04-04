package com.dh.framework.aop.intercept;


import com.dh.framework.aop.aspect.DhJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法调用实现类
 * 封装了目标方法的调用，并维护了拦截器链
 */
public class DhMethodInvocation implements DhJoinPoint {

    /**
     * 代理对象
     */
    protected final Object proxy;

    /**
     * 目标对象
     */
    protected final Object target;

    /**
     * 目标方法
     */
    protected final Method method;

    /**
     * 方法参数
     */
    protected Object[] arguments;

    /**
     * 目标类
     */
    private final Class<?> targetClass;

    /**
     * 用户自定义属性
     */
    private Map<String, Object> userAttributes = new HashMap<>();

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
            this.proxy = proxy;
            this.target = target;
            this.targetClass = targetClass;
            this.method = method;
            this.arguments = arguments;
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
        userAttributes.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return userAttributes.get(key);
    }

}
