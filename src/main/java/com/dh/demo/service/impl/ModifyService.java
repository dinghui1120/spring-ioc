package com.dh.demo.service.impl;


import com.dh.demo.service.IModifyService;
import com.dh.demo.service.IQueryService;
import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.annotation.DhService;

/**
 * 增删改业务
 * @author: dh
 * @date: 2024年12月01日
 **/
@DhService("")
public class ModifyService implements IModifyService {

	@DhAutowired
	private IQueryService queryService;

	/**
	 * 增加
	 */
	public String add(String name, String address) {
		return "modifyService add,name=" + name + ",address=" + address;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id, String name) {
		queryService.query("bb");
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
