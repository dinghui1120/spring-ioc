package com.dh.demo.controller;


import com.dh.demo.service.IModifyService;
import com.dh.demo.service.IQueryService;
import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.annotation.DhController;
import com.dh.framework.annotation.DhRequestMapping;
import com.dh.framework.annotation.DhRequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * demo
 * @author: dh
 * @date: 2024年12月01日
 **/
@DhController
@DhRequestMapping("demo")
public class DemoController {

	@DhAutowired
	IQueryService queryService;
	@DhAutowired
	IModifyService modifyService;

	@DhRequestMapping("query")
	public void query(HttpServletResponse response, @DhRequestParam("name") String name) {
		String result = queryService.query(name);
		out(response, result);
	}

	@DhRequestMapping("add")
	public void add(HttpServletResponse response, @DhRequestParam("name") String name, @DhRequestParam("address") String address) {
		String result = modifyService.add(name, address);
		out(response, result);
	}

	@DhRequestMapping("remove")
	public void remove(HttpServletResponse response, @DhRequestParam("id") Integer id) {
		String result = modifyService.remove(id);
		out(response, result);
	}

	@DhRequestMapping("edit")
	public void edit(HttpServletResponse response, @DhRequestParam("id") Integer id, @DhRequestParam("name") String name) {
		String result = modifyService.edit(id, name);
		out(response, result);
	}


	private void out(HttpServletResponse resp, String str) {
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
