package com.dh.framework.aop;


import com.dh.framework.aop.support.DhAdvisedSupport;

/**
 * AOP代理工厂
 * 根据目标类情况选择合适的代理方式
 */
public class DhDefaultAopProxyFactory {

    /**
     * 创建AOP代理实例
     * 如果目标类有实现接口则使用JDK动态代理
     * 否则使用CGLIB代理
     */
    public DhAopProxy createAopProxy(DhAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new DhJdkDynamicAopProxy(config);
        }
        return new DhCglibAopProxy(config);
    }

}
