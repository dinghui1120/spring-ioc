package com.dh.framework.aop;


import com.dh.framework.aop.support.DhAdvisedSupport;

public class DhDefaultAopProxyFactory {

    public DhAopProxy createAopProxy(DhAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new DhJdkDynamicAopProxy(config);
        }
        return new DhCglibAopPorxy();
    }

}
