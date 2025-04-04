package com.dh.framework.aop.aspect;

/**
 * 用于环绕通知的连接点接口
 * 与JoinPoint不同，ProceedingJoinPoint提供了proceed方法来控制是否执行目标方法
 */
public interface DhProceedingJoinPoint extends DhJoinPoint {
    
    /**
     * 继续执行连接点的方法调用
     * 在环绕通知中使用，用于控制目标方法的执行
     * @return 方法执行的返回值
     * @throws Throwable 可能抛出的异常
     */
    Object proceed() throws Throwable;
    
    /**
     * 使用新的参数继续执行连接点的方法调用
     * @param args 新的方法参数
     * @return 方法执行的返回值
     * @throws Throwable 可能抛出的异常
     */
    Object proceed(Object[] args) throws Throwable;

} 