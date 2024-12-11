package com.dh.framework.annotation;

import java.lang.annotation.*;


/**
 * 自动注入
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DhAutowired {

	String value() default "";

}
