package com.dh.demo.aspect;

import com.dh.framework.aop.aspect.DhJoinPoint;
import com.dh.framework.aop.aspect.DhProceedingJoinPoint;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;


@Slf4j
public class LogAspect {

    public void before(DhJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        log.info("Invoke Before Method,methodName:{}", joinPoint.getMethod().getName());
    }

    public void afterReturning(DhJoinPoint joinPoint, Object result) {
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("Invoke After Returning Method,\n methodName:{},\n result:{},\n cost time:{}",
                joinPoint.getMethod().getName(), result, (endTime - startTime) + "ms");
    }

    public void afterThrowing(DhJoinPoint joinPoint, Throwable ex) {
        log.info("Invoke After Throwing Method,methodName:{},e:{}", joinPoint.getMethod().getName(), ex);
    }

    public void after(DhJoinPoint joinPoint) {
        log.info("后置通知 - 方法执行: {}", joinPoint.getMethod().getName());
    }


    public Object around(DhProceedingJoinPoint pjp) throws Throwable {
        Method method = pjp.getMethod();
        String methodName = method.getName();
        Object[] args = pjp.getArguments();
        
        log.info("环绕通知 - 方法开始: {}, 参数: {}", methodName, args);
        long startTime = System.currentTimeMillis();
        
        Object result;
        try {
            // 可以在这里修改方法参数
//            if (args != null) {
//                args[0] = "修改后参数";
//            }
//            result = pjp.proceed(args);
            
            // 执行目标方法
            result = pjp.proceed();
            log.info("环绕通知 - 方法正常返回: {}, 返回值: {}", methodName, result);
            
            // 可以在这里修改返回值
            // result = "修改后返回值";
        } catch (Throwable ex) {
            log.error("环绕通知 - 方法异常: {}, 异常信息: {}", methodName, ex.getMessage());
            throw ex;
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("环绕通知 - 方法结束: {}, 耗时: {}ms", methodName, (endTime - startTime));
        }
        return result;
    }

}
