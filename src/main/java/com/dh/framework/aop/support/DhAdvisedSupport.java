package com.dh.framework.aop.support;

import com.dh.framework.aop.aspect.DhAfterReturningAdviceInterceptor;
import com.dh.framework.aop.aspect.DhAspectJAfterThrowingAdvice;
import com.dh.framework.aop.aspect.DhMethodBeforeAdviceInterceptor;
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
    private DhAopConfig config;
    private Object target;
    private Class targetClass;
    private Pattern pointCutClassPattern;

    private Map<Method, List<Object>> methodCache;

    public DhAdvisedSupport(DhAopConfig config) {
        this.config = config;
    }

    //解析配置文件的方法
    private void parse() {
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

        //享元的共享池  绑定关系
        methodCache = new HashMap<>();
        //匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);
        try {
            Class aspectClass = Class.forName(config.getAspectClass());
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
                    List<Object> advices = new LinkedList<>();
                    if (!(null == config.getAspectAfterReturn() || "".equals(config.getAspectAfterReturn()))) {
                        advices.add(new DhAfterReturningAdviceInterceptor(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterReturn())));
                    }
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        DhAspectJAfterThrowingAdvice advice = new DhAspectJAfterThrowingAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(advice);
                    }
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.add(new DhMethodBeforeAdviceInterceptor(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }
                    //跟目标代理类的业务方法和Advices建立一对多个关联关系，以便在Porxy类中获得
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws  Exception{
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            methodCache.put(method, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
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

}
