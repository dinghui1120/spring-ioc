package com.dh.framework.webmvc.servlet;

import java.util.Map;

public class DhModelAndView {
    private String viewName;
    private Map<String,?> model;

    public DhModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public DhModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
