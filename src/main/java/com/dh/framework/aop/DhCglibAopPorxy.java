package com.dh.framework.aop;

/**
 * 基于CGLIB的AOP代理实现
 * 用于目标类没有实现接口的情况
 * 通过继承方式实现代理
 */
public class DhCglibAopPorxy implements DhAopProxy {

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }

}
