package com.dh.framework.aop;

public interface DhAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
