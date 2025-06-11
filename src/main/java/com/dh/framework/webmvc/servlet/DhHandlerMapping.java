package com.dh.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class DhHandlerMapping {

    private Object controller;
    protected Method method;
    protected Pattern pattern;

    public DhHandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Object getController() {
        return controller;
    }
}
