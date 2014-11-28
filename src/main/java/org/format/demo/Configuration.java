package org.format.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

@Component
public class Configuration implements BeanFactoryAware {

    public static BeanFactory beanFactory;

    public static String SESSION_LOGIN = "LOGIN_NAME";

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Configuration.beanFactory = beanFactory;
    }

}
