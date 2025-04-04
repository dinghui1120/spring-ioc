package com.dh.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 为各种通知提供基础实现
 */
public abstract class DhAbstractAspectJAdvice implements DhAdvice {

    protected Object aspect;
    protected Method adviceMethod;

    public DhAbstractAspectJAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    /**
     * 调用通知方法
     */
    protected Object invokeAdviceMethod(DhJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {
        Class<?> [] paramTypes = adviceMethod.getParameterTypes();
        if (paramTypes.length == 0) {
            return adviceMethod.invoke(aspect);
        }
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] == DhJoinPoint.class) {
                args[i] = joinPoint;
            } else if (paramTypes[i] == Object.class) {
                args[i] = returnValue;
            } else if (ex != null) {
                bindExceptionParameter(args, i, paramTypes[i], ex);
            }
        }
        return adviceMethod.invoke(aspect, args);
    }
    
    /**
     * 绑定异常参数的模板方法
     * 默认实现是根据类型兼容性将异常对象传递给参数
     * 子类可以重写此方法实现特定的绑定逻辑
     */
    protected void bindExceptionParameter(Object[] args, int paramIndex, Class<?> paramType, Throwable ex) {
        if (paramType.isAssignableFrom(ex.getClass())) {
            args[paramIndex] = ex;
        }
    }
}
