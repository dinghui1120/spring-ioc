package com.dh.framework.aop;

import com.dh.framework.aop.intercept.DhMethodInvocation;
import com.dh.framework.aop.support.DhAdvisedSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 基于CGLIB的AOP代理实现
 * 用于目标类没有实现接口的情况
 * 通过继承方式实现代理
 */
@Slf4j
public class DhCglibAopProxy implements DhAopProxy, MethodInterceptor {

    private DhAdvisedSupport advised;
    
    public DhCglibAopProxy(DhAdvisedSupport config) {
        this.advised = config;
    }

    /**
     * CGLIB MethodInterceptor接口方法
     * 拦截代理对象的方法调用
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        try {
            // 获取方法对应的拦截器链
            List<Object> chain = advised.getInterceptorsAndDynamicInterceptionAdvice(method, advised.getTargetClass());
            // 如果没有拦截器，直接调用目标方法
            if (chain == null || chain.isEmpty()) {
                return methodProxy.invoke(advised.getTarget(), args);
            }
            // 创建方法调用对象，并执行拦截器链
            DhMethodInvocation mi = new DhMethodInvocation(proxy, advised.getTarget(), method, args,
                    advised.getTargetClass(), chain);
            return mi.proceed();
        } catch (Exception e) {
            log.error("CGLIB代理执行方法时发生异常:", e);
            throw e;
        }
    }
    
    @Override
    public Object getProxy() {
        return getProxy(advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (advised.getTargetClass() == null) {
            throw new IllegalArgumentException("目标类不能为null");
        }

        try {
            // 创建CGLIB Enhancer
            Enhancer enhancer = new Enhancer();
            // 设置代理类的父类
            enhancer.setSuperclass(advised.getTargetClass());
            // 设置回调
            enhancer.setCallback(this);
            // 设置类加载器
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
            }
            // 创建代理对象
            return enhancer.create();
        } catch (Exception e) {
            log.error("创建CGLIB代理时发生异常:", e);
            throw e;
        }
    }
} 