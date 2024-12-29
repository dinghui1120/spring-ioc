package com.dh.demo.service.impl;


import com.dh.demo.service.IModifyService;
import com.dh.demo.service.IQueryService;
import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.annotation.DhService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 * @author: dh
 * @date: 2024年12月01日
 **/
@Slf4j
@DhService
public class QueryService implements IQueryService {

	@DhAutowired
	IModifyService modifyService;

	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		log.info("这是在业务方法中打印的：" + json);
		return json;
	}

}
