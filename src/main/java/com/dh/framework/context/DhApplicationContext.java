package com.dh.framework.context;



import com.dh.framework.annotation.DhAutowired;
import com.dh.framework.beans.DhBeanWrapper;
import com.dh.framework.beans.config.DhBeanDefinition;
import com.dh.framework.beans.support.DhBeanDefinitionReader;
import com.dh.framework.beans.support.DhDefaultListableBeanFactory;
import com.dh.framework.core.DhBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DhApplicationContext implements DhBeanFactory {

    private DhDefaultListableBeanFactory registry = new DhDefaultListableBeanFactory();

    //三级缓存（终极缓存）
    private Map<String, DhBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    private Map<String,Object> factoryBeanObjectCache = new HashMap<>();

    public DhApplicationContext(String... configLocations) {
        try {
            //1、加载解析配置文件
            DhBeanDefinitionReader reader = new DhBeanDefinitionReader(configLocations);
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
            if(!entry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }

    @Override
    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) {
        //1、先拿到BeanDefinition配置信息
        DhBeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        //2、反射实例化对象
        Object instance = instantiateBean(beanName, beanDefinition);
        //3、将返回的Bean的对象封装成BeanWrapper
        DhBeanWrapper beanWrapper = new DhBeanWrapper(instance);
        //4、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);
        //5、保存到IoC容器中
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        return beanWrapper.getWrapperInstance();
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
            //如果是代理对象,触发AOP的逻辑
            factoryBeanObjectCache.put(beanName, instance);
        }catch (Exception e){
            e.printStackTrace();
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
                autowiredBeanName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            try {
                //  A  --->  实例化 ---> 放入到容器 --->  属性注入 --> 把A放入到factoryBeanInstanceCache容器里面去了
                //  B  --->  实例化 ---> 放入到容器 --->  属性注入A(完成) --> 然后把B放入到factoryBeanInstanceCache里面去了
                if(factoryBeanInstanceCache.get(autowiredBeanName) == null){
                    continue;
                }
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
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

}
