package com.dh.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: dh
 * @date: 2024年12月14日
 **/
@EnableAspectJAutoProxy
public class SpringTest {

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTest.class);
        applicationContext.getBean("");
//        String s = "public .* com.dh.demo.service..*Service..*(.*)";
////        s = "public .* com.gupaoedu.vip.demo.service..*Service..*(.*)";
//
//        String pointCut = s
//                .replaceAll("\\.", "\\\\.")
//                .replaceAll("\\\\.\\*", ".*")
//                .replaceAll("\\(", "\\\\(")
//                .replaceAll("\\)", "\\\\)");
//        System.out.println(pointCut);
//        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") -4);
//        System.out.println(pointCutForClassRegex);
//        String pointCutClass = "class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1);
//        System.out.println(pointCutClass);
    }

}
