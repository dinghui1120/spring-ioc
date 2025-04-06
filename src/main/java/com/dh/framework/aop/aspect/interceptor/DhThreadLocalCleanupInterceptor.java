package com.dh.framework.aop.aspect.interceptor;

import com.dh.framework.aop.aspect.joinpoint.JoinPointContext;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocal cleanup interceptor
 * Used to clean ThreadLocal resources after all advices have executed
 * Should be the last interceptor in the chain
 */
@Slf4j
public class DhThreadLocalCleanupInterceptor implements DhMethodInterceptor {

    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            // Execute method first to ensure all other interceptors have executed
            return mi.proceed();
        } finally {
            // All advices have executed, now it's safe to clean ThreadLocal resources
            // Only clean in root invocation to avoid nested call issues
            if (mi.isRootInvocation()) {
                log.debug("Cleaning ThreadLocal resources, method: {}, attributes: {}", 
                          mi.getMethod().getName(), 
                          JoinPointContext.getAttributes());
                JoinPointContext.clear();
            } else {
                log.debug("Inner call, skipping ThreadLocal cleanup, method: {}", mi.getMethod().getName());
            }
        }
    }
} 