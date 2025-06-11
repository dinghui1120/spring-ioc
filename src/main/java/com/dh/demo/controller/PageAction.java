package com.dh.demo.controller;

import com.dh.demo.service.IQueryService;
import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.annotation.DhController;
import com.dh.framework.annotation.DhRequestMapping;
import com.dh.framework.annotation.DhRequestParam;
import com.dh.framework.webmvc.servlet.DhModelAndView;

import java.util.HashMap;
import java.util.Map;

@DhController
@DhRequestMapping("/")
public class PageAction {

    @DhAutowired
    IQueryService queryService;

    @DhRequestMapping("/first.html")
    public DhModelAndView page(@DhRequestParam("username") String name){
        String result = queryService.query(name);
        Map<String,Object> model = new HashMap<>();
        model.put("username", name);
        model.put("data", result);
        model.put("token", "123456");
        return new DhModelAndView("first.html",model);
    }

}
