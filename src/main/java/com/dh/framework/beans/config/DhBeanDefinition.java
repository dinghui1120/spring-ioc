package com.dh.framework.beans.config;

/**
 * 定义bean相关信息
 */
public class DhBeanDefinition {

    /**
     * beanName
     */
    private String beanName;

    /**
     * 原生类的全类名
     */
    private String beanClassName;

    public boolean isSingleton(){return true;}

    public boolean isLazyInit() {
        return false;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

}
