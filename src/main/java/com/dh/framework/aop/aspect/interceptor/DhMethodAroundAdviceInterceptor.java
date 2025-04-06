package com.dh.framework.aop.aspect.interceptor;

import com.dh.framework.aop.aspect.advice.DhAbstractAspectJAdvice;
import com.dh.framework.aop.aspect.joinpoint.DhMethodInvocationProceedingJoinPoint;
import com.dh.framework.aop.aspect.joinpoint.DhProceedingJoinPoint;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 环绕通知拦截器
 * 在目标方法执行前后都执行自定义逻辑
 */
@Slf4j
public class DhMethodAroundAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    public DhMethodAroundAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    /**
     * 环绕通知的实现
     * 创建ProceedingJoinPoint对象，传给环绕通知方法
     */
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        DhMethodInvocationProceedingJoinPoint pjp = new DhMethodInvocationProceedingJoinPoint(mi);
        // 检查通知方法的第一个参数是否为ProceedingJoinPoint
        Class<?>[] paramTypes = adviceMethod.getParameterTypes();
        if (paramTypes.length == 0 || !DhProceedingJoinPoint.class.isAssignableFrom(paramTypes[0])) {
            throw new IllegalArgumentException("环绕通知方法[" + adviceMethod.getName() + "]的第一个参数必须是DhProceedingJoinPoint类型");
        }
        // 调用环绕通知方法，传入ProceedingJoinPoint
        Object result = adviceMethod.invoke(aspect, pjp);
        // 检查是否已经调用了proceed方法
        if (!pjp.hasProceeded()) {
            log.info("环绕通知方法未调用proceed()，目标方法将不会被执行: {}", adviceMethod.getName());
        }
        return result;
    }
    
} 