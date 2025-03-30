package com.dh.framework.aop.aspect;

import java.lang.reflect.Method;

public interface DhJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);

}
