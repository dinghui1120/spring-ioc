package com.dh.framework.aop;


import com.dh.framework.aop.intercept.DhMethodInvocation;
import com.dh.framework.aop.support.DhAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 基于JDK动态代理的AOP代理实现
 * 用于目标类有实现接口的情况
 */
public class DhJdkDynamicAopProxy implements DhAopProxy, InvocationHandler {

    private DhAdvisedSupport advised;

    public DhJdkDynamicAopProxy(DhAdvisedSupport config) {
        this.advised = config;
    }

    /**
     * 在这里织入切面逻辑
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = advised.getInterceptorChain(method, advised.getTargetClass());
        if (chain == null || chain.isEmpty()) {
            return method.invoke(advised.getTarget(), args);
        }
        DhMethodInvocation mi = new DhMethodInvocation(proxy, advised.getTarget(), method, args,
                advised.getTargetClass(), chain);
        return mi.proceed();
    }

    public Object getProxy() {
        return getProxy(advised.getTargetClass().getClassLoader());
    }

    /**
     * 获取代理对象
     */
    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, advised.getTargetClass().getInterfaces(), this);
    }

}
