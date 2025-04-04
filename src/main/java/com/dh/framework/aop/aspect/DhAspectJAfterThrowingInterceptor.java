package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

/**
 * 异常通知拦截器
 * 在目标方法抛出异常时执行
 */
public class DhAspectJAfterThrowingInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    /**
     * 异常参数名
     */
    private String throwName;
    private int throwingParamIndex = -1;

    public DhAspectJAfterThrowingInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 抛出异常时执行通知方法
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(mi, null, ex);
            throw ex;
        }
    }
    
    /**
     * 重写父类的异常参数绑定方法
     * 实现根据指定位置绑定异常参数
     */
    @Override
    protected void bindExceptionParameter(Object[] args, int paramIndex, Class<?> paramType, Throwable ex) {
        if (throwingParamIndex >= 0) {
            if (paramIndex == throwingParamIndex && paramType.isAssignableFrom(ex.getClass())) {
                args[paramIndex] = ex;
            }
        } else {
            super.bindExceptionParameter(args, paramIndex, paramType, ex);
        }
    }
    
    /**
     * 设置异常参数名
     * 通过参数位置映射实现名称自定义
     * @param throwName 异常参数名
     */
    public void setThrowName(String throwName) {
        this.throwName = throwName;
        if (throwName != null && !throwName.isEmpty()) {
            boolean firstIsJoinPoint = adviceMethod.getParameterTypes().length > 0
                    && adviceMethod.getParameterTypes()[0] == DhJoinPoint.class;
            throwingParamIndex = firstIsJoinPoint ? 1 : 0;
        } else {
            throwingParamIndex = -1;
        }
    }
    
    /**
     * 获取异常参数名
     */
    public String getThrowName() {
        return this.throwName;
    }
}
