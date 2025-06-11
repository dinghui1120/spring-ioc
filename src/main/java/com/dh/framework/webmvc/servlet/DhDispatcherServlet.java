package com.dh.framework.webmvc.servlet;


import com.dh.framework.annotation.DhController;
import com.dh.framework.annotation.DhRequestMapping;
import com.dh.framework.context.DhApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DhDispatcherServlet extends HttpServlet {

    /**
     * 保存Controller中URL和Method的对应关系
     */
    private List<DhHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<DhHandlerMapping, DhHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<DhViewResolver> viewResolvers = new ArrayList<>();

    /**
     * IoC容器的访问上下文
     */
    private DhApplicationContext applicationContext = null;

    @Override
    public void init(ServletConfig config) {
        applicationContext = new DhApplicationContext(config.getInitParameter("contextConfigLocation"));
        //========== MVC  ==========
        initStrategies(applicationContext);
        log.info("dh framework is init.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        //6、根据URL委派给具体的调用方法
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            Map<String,Object> model = new HashMap<>();
            model.put("detail","500 Exception,Detail: ");
            model.put("stackTrace",Arrays.toString(e.getStackTrace()));
            try {
                processDispatchResult(req, resp, new DhModelAndView("500", model));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、根据URL 拿到对应的Handler
        DhHandlerMapping handler = getHandler(req);
        if(null == handler){
            processDispatchResult(req, resp, new DhModelAndView("404"));
            return;
        }
        //2、根据HandlerMapping拿到HandlerAdapter
        DhHandlerAdapter ha = getHandlerAdapter(handler);
        if (null == ha) {
            processDispatchResult(req, resp, new DhModelAndView("404"));
            return;
        }
        //3、根据HandlerAdapter拿到对应的ModelAndView
        DhModelAndView mv = ha.handle(req, resp, handler);
        //4、根据ViewResolver找到对应View对象
        //通过View对象渲染页面，并返回
        processDispatchResult(req,resp,mv);
    }

    private DhHandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (DhHandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    private DhHandlerAdapter getHandlerAdapter(DhHandlerMapping handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }
        return handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, DhModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }
        if (viewResolvers.isEmpty()) {
            return;
        }
        for (DhViewResolver viewResolver : viewResolvers) {
            DhView view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    /**
     * 初始化策略
     * @param context
     */
    protected void initStrategies(DhApplicationContext context) {
        //handlerMapping
        initHandlerMappings();
        //初始化参数适配器
        initHandlerAdapters();
        //初始化视图解析器
        initViewResolvers(context);
    }

    /**
     * 初始化HandlerMapping
     */
    private void initHandlerMappings() {
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
                //  //demo//query
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("\\*",".*")
                        .replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new DhHandlerMapping(pattern, instance, method));
                log.info("Mapped : " + regex + " --> " + method);
            }
        }
    }

    /**
     * 初始化参数适配器
     */
    private void initHandlerAdapters() {
        for (DhHandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new DhHandlerAdapter());
        }
    }

    /**
     * 初始化视图解析器
     * @param context
     */
    private void initViewResolvers(DhApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        viewResolvers.add(new DhViewResolver(templateRoot));
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