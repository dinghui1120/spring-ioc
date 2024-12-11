package com.dh.framework.webmvc.servlet;


import com.dh.framework.annotation.DhController;
import com.dh.framework.annotation.DhRequestMapping;
import com.dh.framework.annotation.DhRequestParam;
import com.dh.framework.context.DhApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DhDispatcherServlet extends HttpServlet {

    /**
     * 保存Controller中URL和Method的对应关系
     */
    private Map<String, Method> handlerMapping = new HashMap<>();

    /**
     * IoC容器的访问上下文
     */
    private DhApplicationContext applicationContext = null;

    @Override
    public void init(ServletConfig config) {
        applicationContext = new DhApplicationContext(config.getInitParameter("contextConfigLocation"));
        //========== MVC功能 ==========
        //5、初始化HandlerMapping
        doInitHandlerMapping();
        System.out.println("dh framework is init.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //6、根据URL委派给具体的调用方法
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception,Detail: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //  /spring-ioc/web/query
        String url = req.getRequestURI();
        // /spring-ioc
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!!");
            return;
        }
        Method method = handlerMapping.get(url);
        //1、先把形参的位置和参数名字建立映射关系，并且缓存下来
        Map<String,Integer> paramIndexMapping = new HashMap<>();
        Annotation [][] pa = method.getParameterAnnotations();
        for (int i = 0; i < pa.length; i ++) {
            for (Annotation a : pa[i]) {
                if(a instanceof DhRequestParam){
                    String paramName = ((DhRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
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

        //http://localhost/demo/query?name=Tom&name=Tomcat&name=Mic
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
            paramValues[index] = value;
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        //3、组成动态实际参数列表，传给反射调用
        method.invoke(applicationContext.getBean(beanName), paramValues);
    }



    /**
     * 初始化HandlerMapping
     */
    private void doInitHandlerMapping() {
        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            if (!clazz.isAnnotationPresent(DhController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(DhRequestMapping.class)) {
                DhRequestMapping requestMapping = clazz.getAnnotation(DhRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //只迭代public方法
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(DhRequestMapping.class)) {
                    continue;
                }
                DhRequestMapping requestMapping = method.getAnnotation(DhRequestMapping.class);
                // /demo/query
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+","/");
                handlerMapping.put(url, method);
                System.out.println("Mapped : " + url + " --> " + method);
            }
        }
    }

    /**
     * 转为小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        ////利用了ASCII码，大写字母和小写相差32这个规律
        chars[0] += 32;
        return String.valueOf(chars);
    }

}