package com.dh.framework.annotation;

import java.lang.annotation.*;

/**
 * 标注服务层的组件
 * 服务层通常用于封装业务逻辑，是连接控制器（Controller）和数据访问层（DAO）的桥梁
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DhService {

	String value() default "";

}
