package com.dh.framework.annotation;

import java.lang.annotation.*;

/**
 * 用于建立URL路径与控制器方法之间的映射关系。
 * 它既可以标注在类上（表示该控制器处理的基础路径），也可以标注在方法上（表示具体的请求路径和处理方法）
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DhRequestMapping {

	String value() default "";

}
