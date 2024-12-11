package com.dh.framework.beans;

public class DhBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public DhBeanWrapper(Object instance) {
        this.wrapperInstance = instance;
        this.wrapperClass = instance.getClass();
    }

    public Object getWrapperInstance(){
        return this.wrapperInstance;
    }

    public Class<?> getWrapperClass(){
        return this.wrapperClass;
    }

}
