package org.format.demo.postprocess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.annotation.Authorization;
import org.format.demo.interceptor.AuthInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
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
        String classUrl = null;
        if(bean.getClass().isAnnotationPresent(Controller.class)) {
            classAuthAnno = bean.getClass().getAnnotation(Authorization.class);
            RequestMapping classMapping = bean.getClass().getAnnotation(RequestMapping.class);
            if(classMapping.value().length > 0) {
                classUrl = classMapping.value()[0];
            }
            if(log.isInfoEnabled()) {
                log.info("class: " + bean.getClass() + ", annotaion: " + classAuthAnno);
            }
        } else {
            return bean;
        }

        if(classUrl != null) {
            AuthInterceptor authInterceptor = new AuthInterceptor();
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
            List<String> mapping = new ArrayList<String>(methods.length);
            for(Method method : methods) {
                List<String> roleAuth = new ArrayList<String>(roles);
                List<String> authAuth = new ArrayList<String>(auth);
                if(method.isAnnotationPresent(Authorization.class) && method.isAnnotationPresent(RequestMapping.class)) {
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

                    String methodUrl = null;
                    RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                    if(methodMapping.value().length > 0) {
                        methodUrl = methodMapping.value()[0];
                    }
                    if(!CollectionUtils.isEmpty(roleAuth) && !CollectionUtils.isEmpty(authAuth) && methodUrl != null) {
                        authInterceptor.addAuth(classUrl + methodUrl, roleAuth, authAuth);
                    }
                } else {
                    if(log.isInfoEnabled()) {
                        log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                                "has no Authorization Annotaion");
                    }
                }
            }

            //TODO 每个带有RequestMapping和Controller注解的控制器需要加入拦截器
            RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)beanFactory.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0");
            Field interceptorField = null;
            List<MappedInterceptor> mappedInterceptorList = null;
            try {
                interceptorField = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("mappedInterceptors");
                interceptorField.setAccessible(true);
                mappedInterceptorList = (List<MappedInterceptor>)interceptorField.get(requestMappingHandlerMapping);
                mappedInterceptorList.add(new MappedInterceptor(new String[] {classUrl + "/**"}, authInterceptor));
                ReflectionUtils.setField(interceptorField, requestMappingHandlerMapping, mappedInterceptorList);
            } catch (Exception e) {
                log.error("RequestMappingHandlerMapping reflect field error", e);
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
