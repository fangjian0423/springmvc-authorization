package org.format.demo.postprocess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.annotation.Authorization;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthPostProcess implements BeanPostProcessor, BeanFactoryAware {

    private static Log log = LogFactory.getLog(AuthPostProcess.class);
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        Authorization classAuthAnno = null;
        if(bean.getClass().isAnnotationPresent(Controller.class)) {
            classAuthAnno = bean.getClass().getAnnotation(Authorization.class);
            if(log.isInfoEnabled()) {
                log.info("class: " + bean.getClass() + ", annotaion: " + classAuthAnno);
            }
        } else {
            return bean;
        }

        List<String> roles = new ArrayList<String>();
        List<String> auth = new ArrayList<String>();

        if(classAuthAnno != null) {
            if(classAuthAnno.auth().length > 0) {
                auth.addAll(Arrays.asList(classAuthAnno.auth()));
            }
            if(classAuthAnno.roles().length > 0) {
                roles.addAll(Arrays.asList(classAuthAnno.roles()));
            }
        }

        Method[] methods = bean.getClass().getDeclaredMethods();

        for(Method method : methods) {
            List<String> roleAuth = new ArrayList<String>(roles);
            List<String> authAuth = new ArrayList<String>(auth);
            if(method.isAnnotationPresent(Authorization.class)) {
                if(log.isInfoEnabled()) {
                    log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                                "annotaion: " + method.getAnnotation(Authorization.class));
                }
                Authorization methodAuth = method.getAnnotation(Authorization.class);
                if(methodAuth.roles().length > 0) {
                    roleAuth.addAll(Arrays.asList(methodAuth.roles()));
                }
                if(methodAuth.auth().length > 0) {
                    authAuth.addAll(Arrays.asList(methodAuth.auth()));
                }
            } else {
                if(log.isInfoEnabled()) {
                    log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                            "has no Authorization Annotaion");
                }
            }

        }


        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
