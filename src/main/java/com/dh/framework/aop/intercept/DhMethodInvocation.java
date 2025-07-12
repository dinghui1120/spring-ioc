package com.dh.framework.aop.intercept;

import com.dh.framework.aop.aspect.joinpoint.DhAbstractJoinPoint;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法调用实现类
 * 封装了目标方法的调用，并维护了拦截器链
 */
public class DhMethodInvocation extends DhAbstractJoinPoint {

    /**
     * 代理对象
     */
    protected final Object proxy;

    /**
     * 拦截器链
     */
    protected final List<?> interceptorChain;

    /**
     * 当前执行到的拦截器索引
     */
    private int currentInterceptorIndex = -1;

    /**
     * 是否是最外层调用
     * 用于确定何时清理ThreadLocal资源
     */
    private final boolean isRootInvocation;

    public DhMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers, true);
    }
    
    protected DhMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
                Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, boolean isRootInvocation) {
        super(target, method, arguments, targetClass);
        this.proxy = proxy;
        this.interceptorChain = interceptorsAndDynamicMethodMatchers;
        this.isRootInvocation = isRootInvocation;
    }

    /**
     * 执行方法调用
     */
    public Object proceed() throws Throwable {
        if (currentInterceptorIndex == interceptorChain.size() - 1) {
            return method.invoke(target, arguments);
        }
        Object interceptor = interceptorChain.get(++currentInterceptorIndex);
        if (interceptor instanceof DhMethodInterceptor) {
            DhMethodInterceptor mi = (DhMethodInterceptor) interceptor;
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

    /**
     * 检查当前调用是否为最外层调用
     * 用于确定是否应该清理ThreadLocal资源
     */
    public boolean isRootInvocation() {
        return isRootInvocation;
    }

}
