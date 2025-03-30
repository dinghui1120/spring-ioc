package com.dh.framework.aop.aspect;


import com.dh.framework.aop.intercept.DhMethodInterceptor;
import com.dh.framework.aop.intercept.DhMethodInvocation;

import java.lang.reflect.Method;

public class DhMethodBeforeAdviceInterceptor extends DhAbstractAspectJAdvice implements DhMethodInterceptor {

    private DhJoinPoint jp;
    public DhMethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    public void before(Method method, Object[] arguments, Object aThis) throws Throwable{
        invokeAdviceMethod(this.jp,null,null);
    }

    // 还没有调用到方法  before  能先执行before方法，是因为before方法放在mi.proceed()前面
    @Override
    public Object invoke(DhMethodInvocation mi) throws Throwable {
        jp = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();

    }
}
