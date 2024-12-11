package com.dh.framework.beans.support;


import com.dh.framework.beans.config.DhBeanDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DhBeanDefinitionReader {

    /**
     * 配置文件
     */
    private Properties contextConfig = new Properties();

    /**
     * 缓存从包路径下扫描的全类名, 需要被注册的BeanClass
     */
    private List<String> registryBeanClasses = new ArrayList<>();

    public DhBeanDefinitionReader(String... locations) {
        //1、加载Properties文件
        doLoadConfig(locations[0]);
        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    /**
     * 去ClassPath下加载对应的配置文件
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        String configPath = contextConfigLocation.replaceAll("classpath:", "");
        // 使用 try-with-resources 自动管理输入流
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("配置文件不存在: " + configPath);
            }
            contextConfig.load(inputStream);
        } catch (IOException e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 扫描ClassPath下符合包路径规则所有的Class文件
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        String path = "/" + scanPackage.replaceAll("\\.","/");
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("包路径不存在: " + scanPackage);
        }
        File classPath = new File(url.getFile());
        if (!classPath.exists() || !classPath.isDirectory()) {
            throw new IllegalArgumentException("无效的类路径: " + classPath.getAbsolutePath());
        }
        File[] files = classPath.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
                continue;
            }
            // 过滤并处理 .class 文件
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            // 包名.类名，例如 com.dh.demo.controller.DemoController
            String className = scanPackage + "." + file.getName().replace(".class", "");
            // 添加到注册类列表中
            registryBeanClasses.add(className);
        }
    }

    /**
     * 封装成BeanDefinition对象
     * @return
     */
    public List<DhBeanDefinition> loadBeanDefinitions() {
        List<DhBeanDefinition> beanDefinitionList = new ArrayList<>();
        for (String className : registryBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                // 如果 beanClass 是接口，则跳过处理
                if (beanClass.isInterface()) {
                    continue;
                }
                // 默认类名首字母小写
                beanDefinitionList.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                // 如果是接口，就用实现类
                for (Class<?> clazz : beanClass.getInterfaces()) {
                    beanDefinitionList.add(doCreateBeanDefinition(clazz.getName(), beanClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                System.err.println("类没找到: " + className);
            } catch (Exception e) {
                System.err.println("封装BeanDefinition异常: " + className);
                e.printStackTrace();
            }
        }
        return beanDefinitionList;
    }


    /**
     * 创建BeanDefinition
     * @param beanName
     * @param beanClassName
     * @return
     */
    private DhBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        DhBeanDefinition beanDefinition = new DhBeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }


    /**
     * 转为小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //利用了ASCII码，大写字母和小写相差32这个规律
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
