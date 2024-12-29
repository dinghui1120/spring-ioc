package com.dh.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: dh
 * @date: 2024年12月14日
 **/
public class SpringTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTest.class);
        applicationContext.getBean("");
    }

}
