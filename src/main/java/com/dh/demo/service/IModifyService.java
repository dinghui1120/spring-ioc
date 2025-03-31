package com.dh.demo.service;

/**
 * 增删改业务
 * @author: dh
 * @date: 2024年12月01日
 **/
public interface IModifyService {

	/**
	 * 增加
	 */
	String add(String name, String address);
	
	/**
	 * 修改
	 */
	String edit(Integer id, String name);
	
	/**
	 * 删除
	 */
	String remove(Integer id) throws Exception;
	
}
