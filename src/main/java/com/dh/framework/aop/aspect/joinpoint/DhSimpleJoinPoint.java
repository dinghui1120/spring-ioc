package com.dh.framework.aop.aspect.joinpoint;

import java.lang.reflect.Method;

/**
 * 知DhJoinPoint的简单实现，用于非环绕通
 * 不包含proceed方法，避免非环绕通知调用proceed导致的问题
 */
public class DhSimpleJoinPoint extends DhAbstractJoinPoint {

    public DhSimpleJoinPoint(Object target, Method method, Object[] arguments, Class<?> targetClass) {
        super(target, method, arguments, targetClass);
    }

} 