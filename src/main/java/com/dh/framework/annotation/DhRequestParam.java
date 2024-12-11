package com.dh.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求参数映射
 * 用于将请求参数绑定到控制器方法的参数上。
 * 它通常用于处理HTTP请求中的查询参数或表单参数。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DhRequestParam {
	
	String value() default "";
	
	boolean required() default true;

}
