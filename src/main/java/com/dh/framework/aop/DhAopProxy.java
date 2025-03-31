package com.dh.framework.aop;

/**
 * AOP代理接口
 */
public interface DhAopProxy {

    /**
     * 获取代理对象，使用默认的类加载器
     */
    Object getProxy();

    /**
     * 获取代理对象，使用指定的类加载器
     */
    Object getProxy(ClassLoader classLoader);

}
