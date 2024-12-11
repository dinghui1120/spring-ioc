package com.dh.framework.annotation;

import java.lang.annotation.*;


/**
 * 控制器
 * 处理由前端发送过来的HTTP请求，并返回相应的响应，
 * 这通常包括视图名（用于渲染页面）或数据（如JSON、XML等）
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DhController {

	String value() default "";

}
