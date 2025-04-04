package com.dh.framework.aop.support;

import com.dh.framework.aop.aspect.*;
import com.dh.framework.aop.config.DhAopConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析AOP配置的工具类
 */
@Slf4j
public class DhAdvisedSupport {

    /**
     * AOP配置信息
     */
    private DhAopConfig config;

    /**
     * 目标对象
     */
    private Object target;

    /**
     * 目标类
     */
    private Class targetClass;

    /**
     * 切点类正则表达式
     */
    private Pattern pointCutClassPattern;

    /**
     * 方法对应的拦截器链
     */
    private Map<Method, List<Object>> methodCache;

    public DhAdvisedSupport(DhAopConfig config) {
        this.config = config;
    }

    /**
     * 解析配置文件，构建拦截器链
     */
    public void parse() {
        //把Spring的Expression变成Java能够识别的正则表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        //匹配Class的正则
        //public .* com\.dh\.demo\.service\..*Service
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        //class com\.dh\.demo\.service\..*Service
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        methodCache = new HashMap<>();
        //匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);
        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            for (Method method : targetClass.getMethods()) {
                //public java.lang.String com.dh.demo.service.impl.QueryService.query(java.lang.String)
                //public java.lang.String com.dh.demo.service.impl.ModifyService.remove(java.lang.Integer) throws java.lang.Exception
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pointCutPattern.matcher(methodString);
                if (matcher.matches()) {
                    buildAdviceChain(method, aspectClass, aspectMethods);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为匹配的方法构建拦截器链
     */
    private void buildAdviceChain(Method method, Class<?> aspectClass, Map<String, Method> aspectMethods) {
        List<Object> advices = new LinkedList<>();
        try {
            Object aspectInstance = aspectClass.newInstance();
            // 添加环绕通知
            String aroundMethod = config.getAspectAround();
            if (isValidMethod(aroundMethod)) {
                Method aspectMethod = aspectMethods.get(aroundMethod);
                if (aspectMethod != null) {
                    advices.add(new DhMethodAroundAdviceInterceptor(aspectInstance, aspectMethod));
                }
            }
            // 添加后置通知
            String afterMethod = config.getAspectAfter();
            if (isValidMethod(afterMethod)) {
                Method aspectMethod = aspectMethods.get(afterMethod);
                if (aspectMethod != null) {
                    advices.add(new DhAfterAdviceInterceptor(aspectInstance, aspectMethod));
                }
            }
            // 添加返回通知
            String afterReturnMethod = config.getAspectAfterReturn();
            if (isValidMethod(afterReturnMethod)) {
                Method aspectMethod = aspectMethods.get(afterReturnMethod);
                if (aspectMethod != null) {
                    advices.add(new DhAfterReturningAdviceInterceptor(aspectInstance, aspectMethod));
                }
            }
            // 添加前置通知
            String beforeMethod = config.getAspectBefore();
            if (isValidMethod(beforeMethod)) {
                Method aspectMethod = aspectMethods.get(beforeMethod);
                if (aspectMethod != null) {
                    advices.add(new DhMethodBeforeAdviceInterceptor(aspectInstance, aspectMethod));
                }
            }
            // 添加异常通知
            String afterThrowMethod = config.getAspectAfterThrow();
            if (isValidMethod(afterThrowMethod)) {
                Method aspectMethod = aspectMethods.get(afterThrowMethod);
                if (aspectMethod != null) {
                    DhAspectJAfterThrowingInterceptor advice = new DhAspectJAfterThrowingInterceptor(aspectInstance, aspectMethod);
                    // 设置异常类型
                    String throwType = config.getAspectAfterThrowingType();
                    if (throwType != null) {
                        advice.setThrowType(throwType);
                    }
                    advices.add(advice);
                }
            }
            if (!advices.isEmpty()) {
                methodCache.put(method, advices);
            }
        } catch (Exception e) {
            log.error("创建通知实例失败: {}", e.getMessage());
        }
    }

    /**
     * 获取目标方法对应的拦截器链
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws  Exception{
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            methodCache.put(method, cached);
        }
        return cached;
    }

    /**
     * 目标类是否匹配切点表达式
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    private boolean isValidMethod(String methodName) {
        return methodName != null && !methodName.trim().isEmpty();
    }

}
