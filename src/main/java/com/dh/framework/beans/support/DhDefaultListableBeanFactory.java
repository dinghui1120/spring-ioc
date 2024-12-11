package com.dh.framework.beans.support;



import com.dh.framework.beans.config.DhBeanDefinition;
import com.dh.framework.core.DhBeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DhDefaultListableBeanFactory implements DhBeanFactory {

    public Map<String, DhBeanDefinition> beanDefinitionMap = new HashMap<>();

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    public void doRegisterBeanDefinition(List<DhBeanDefinition> beanDefinitions) throws Exception {
        for (DhBeanDefinition beanDefinition : beanDefinitions) {
            if(beanDefinitionMap.containsKey(beanDefinition.getBeanName())){
                throw new Exception("The " + beanDefinition.getBeanName() + " is exists!!!");
            }
            beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
    }

}
