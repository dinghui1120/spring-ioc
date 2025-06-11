package com.dh.framework.context;


import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.aop.DhDefaultAopProxyFactory;
import com.dh.framework.aop.config.DhAopConfig;
import com.dh.framework.aop.support.DhAdvisedSupport;
import com.dh.framework.beans.DhBeanWrapper;
import com.dh.framework.beans.config.DhBeanDefinition;
import com.dh.framework.beans.support.DhBeanDefinitionReader;
import com.dh.framework.beans.support.DhDefaultListableBeanFactory;
import com.dh.framework.core.DhBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DhApplicationContext implements DhBeanFactory {

    private DhBeanDefinitionReader reader;

    private DhDefaultAopProxyFactory proxyFactory = new DhDefaultAopProxyFactory();

    private DhDefaultListableBeanFactory registry = new DhDefaultListableBeanFactory();

    /**
     * 正在创建bean的名称
     */
    private Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    /**
     * 一级缓存：保存成熟的Bean(IoC)
     */
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 二级缓存：保存早期的Bean
     */
    private Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * 三级缓存
     */
    private Map<String,Object> singletonFactories = new HashMap<>();



    public DhApplicationContext(String... configLocations) {
        try {
            //1、加载解析配置文件
            reader = new DhBeanDefinitionReader(configLocations);
            //2、将所有的配置信息封装成BeanDefinition
            List<DhBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            //3、所有的配置信息缓存起来
            registry.doRegisterBeanDefinition(beanDefinitions);
            //4、加载非延时加载的所有Bean
            doLoadInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 加载非延时加载的所有Bean
     */
    private void doLoadInstance() {
        for (Map.Entry<String, DhBeanDefinition> entry : registry.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if(entry.getValue().isSingleton() && !entry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) {
        //1、先拿到BeanDefinition配置信息
        DhBeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        //先从缓存中获取Bean
        Object singleton = getSingleton(beanName);
        if (singleton != null) {
            return singleton;
        }
        //标记bean正在创建
        singletonsCurrentlyInCreation.add(beanName);
        //2、反射实例化对象
        Object instance = instantiateBean(beanName, beanDefinition);
        //3、将返回的Bean的对象封装成BeanWrapper
        DhBeanWrapper beanWrapper = new DhBeanWrapper(instance);
        //4、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);
        // 把创建标记清空
        singletonsCurrentlyInCreation.remove(beanName);
        //5、保存到IoC容器中
        addSingleton(beanName, instance);
        return beanWrapper.getWrapperInstance();
    }

    /**
     * 从缓存中获取Bean
     * @param beanName
     * @return
     */
    private Object getSingleton(String beanName) {
        //先去一级缓存里面拿
        Object bean = singletonObjects.get(beanName);
        //如果一级缓存中没有，但是又有创建标识，说明就是循环依赖
        if (bean == null && singletonsCurrentlyInCreation.contains(beanName)) {
            //二级缓存
            bean = earlySingletonObjects.get(beanName);
            //如果二级缓存也没有，从三级缓存中拿
            if (bean == null) {
                bean = singletonFactories.get(beanName);
            }
        }
        return bean;
    }

    /**
     * 保存到IoC容器中
     * @param beanName
     * @param instance
     */
    private void addSingleton(String beanName, Object instance) {
        singletonObjects.put(beanName, instance);
        singletonFactories.remove(beanName);
    }

    /**
     * 反射实例化对象
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, DhBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            //加载AOP配置文件
            DhAdvisedSupport config = instantiationAopConfig();
            config.setTargetClass(clazz);
            config.setTarget(instance);
            config.parse();
            //如果满足条件，直接返回Proxy对象
            if (config.pointCutMatch()) {
                instance = proxyFactory.createAopProxy(config).getProxy();
            }
            singletonFactories.put(beanName, instance);
        } catch (Exception e) {
            log.error("实例化对象异常,className: " + className + ",e:" + e);
        }
        return instance;
    }

    /**
     * 依赖注入
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, DhBeanDefinition beanDefinition, DhBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();
        //忽略字段的修饰符
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(DhAutowired.class)) {
                continue;
            }
            DhAutowired autowired = field.getAnnotation(DhAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                //获得字段的完全限定名
                autowiredBeanName = field.getName();
            }
            //强制访问
            field.setAccessible(true);
            try {
                field.set(instance, getBean(autowiredBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public int getBeanDefinitionCount(){
        return registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames(){
        return registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 获取AOP配置
     */
    private DhAdvisedSupport instantiationAopConfig() {
        DhAopConfig config = new DhAopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfterReturn(reader.getConfig().getProperty("aspectAfterReturn"));
        config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingType(reader.getConfig().getProperty("aspectAfterThrowingType"));
        config.setAspectAround(reader.getConfig().getProperty("aspectAround"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        return new DhAdvisedSupport(config);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

}
