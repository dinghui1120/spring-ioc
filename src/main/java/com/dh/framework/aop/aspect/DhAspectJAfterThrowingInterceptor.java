package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 异常通知拦截器
 * 在目标方法抛出异常时执行
 */
@Slf4j
public class DhAspectJAfterThrowingInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    /**
     * 要捕获的异常类型
     */
    private Class<?> throwType;

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
            log.info("configThrowType:{},exType:{}", throwType, ex.getClass());
            // 如果指定了异常类型，则只处理该类型的异常
            if (throwType == null || throwType.isAssignableFrom(ex.getClass())) {
                invokeAdviceMethod(mi, null, ex);
            }
            throw ex;
        }
    }
    
    /**
     * 设置要捕获的异常类型名称
     * 此方法用于指定拦截器只处理特定类型的异常
     * @param typeName 异常类型的全限定名
     */
    public void setThrowType(String typeName) {
        if (typeName != null && !typeName.isEmpty()) {
            try {
                throwType = Class.forName(typeName);
                if (!Throwable.class.isAssignableFrom(throwType)) {
                    log.error("指定的类型 '" + typeName + "' 不是Throwable的子类");
                    throwType = null;
                }
            } catch (ClassNotFoundException e) {
                log.error("无法加载指定的异常类型 '" + typeName + "'");
                throwType = null;
            }
        }
    }
    
    /**
     * 获取要捕获的异常类型
     */
    public Class<?> getThrowType() {
        return throwType;
    }
}
