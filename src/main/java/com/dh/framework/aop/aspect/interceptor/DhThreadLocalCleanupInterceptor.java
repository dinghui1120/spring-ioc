package com.dh.framework.aop.aspect.interceptor;

import com.dh.framework.aop.aspect.joinpoint.JoinPointContext;
import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocal资源清理拦截器
 * 用于在所有通知执行完毕后清理ThreadLocal资源
 */
@Slf4j
public class DhThreadLocalCleanupInterceptor implements DhMethodInterceptor {

    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } finally {
            // 只在最外层调用时清理，避免嵌套调用问题
            if (mi.isRootInvocation()) {
                log.info("正在清理ThreadLocal资源，方法: {}, 属性: {}", mi.getMethod().getName(), JoinPointContext.getAttributes());
                JoinPointContext.clear();
            } else {
                log.info("内部调用，跳过ThreadLocal资源清理，方法: {}", mi.getMethod().getName());
            }
        }
    }

} 