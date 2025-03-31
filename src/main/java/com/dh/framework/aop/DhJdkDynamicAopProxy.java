package com.dh.framework.aop;


import com.dh.framework.aop.intercept.DhMethodInvocation;
import com.dh.framework.aop.support.DhAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class DhJdkDynamicAopProxy implements DhAopProxy, InvocationHandler {

    private DhAdvisedSupport advised;

    public DhJdkDynamicAopProxy(DhAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = advised.getInterceptorsAndDynamicInterceptionAdvice(method, advised.getTargetClass());

        DhMethodInvocation mi = new DhMethodInvocation(proxy, advised.getTarget(), method, args, advised.getTargetClass(), chain);

        return mi.proceed();
    }

    public Object getProxy() {
        return getProxy(advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, advised.getTargetClass().getInterfaces(), this);
    }

}
