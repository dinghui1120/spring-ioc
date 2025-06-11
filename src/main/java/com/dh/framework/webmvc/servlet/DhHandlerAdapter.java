package com.dh.framework.webmvc.servlet;


import com.dh.framework.annotation.DhRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DhHandlerAdapter {

    public DhModelAndView handle(HttpServletRequest req, HttpServletResponse resp, DhHandlerMapping handler) throws Exception {
        Method method = handler.getMethod();
        //1、先把形参的位置和参数名字建立映射关系，并且缓存下来
        Map<String,Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] pa = method.getParameterAnnotations();
        for (int i = 0; i < pa.length; i ++) {
            for (Annotation a : pa[i]) {
                if(a instanceof DhRequestParam){
                    String paramName = ((DhRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?> [] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(), i);
            }
        }

        //2、根据参数位置匹配参数名字，从url中取到参数名字对应的值
        Object[] paramValues = new Object[paramTypes.length];

        //http://localhost/demo/query?name=A&name=B
        Map<String,String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());
            //涉及到类型强制转换
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object result = method.invoke(handler.getController(), paramValues);
        if (result == null) {
            return null;
        }
        boolean isModelAndView = handler.getMethod().getReturnType() == DhModelAndView.class;
        if (isModelAndView) {
            return (DhModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }
        if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            return Double.valueOf(value);
        }else {
            return value;
        }
    }

}
