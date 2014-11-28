package org.format.demo.postprocess;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.annotation.Authorization;
import org.format.demo.interceptor.AuthInterceptor;
import org.format.demo.model.AuthMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class AuthPostProcess implements BeanPostProcessor, BeanFactoryAware {

    private static Log log = LogFactory.getLog(AuthPostProcess.class);
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        Authorization classAuthAnno = null;
        AuthMode classMode = null;
        List<String> classUrls = new ArrayList<String>();
        if(bean.getClass().isAnnotationPresent(Controller.class)) {
            classAuthAnno = bean.getClass().getAnnotation(Authorization.class);
            RequestMapping classMapping = bean.getClass().getAnnotation(RequestMapping.class);
            if(classMapping.value().length > 0) {
                classUrls.addAll(Arrays.asList(classMapping.value()));
            }
            if(log.isInfoEnabled()) {
                log.info("class: " + bean.getClass() + ", annotaion: " + classAuthAnno);
            }
        } else {
            return bean;
        }

        if(!classUrls.isEmpty()) {
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
                classMode = classAuthAnno.mode();
            }

            Method[] methods = bean.getClass().getDeclaredMethods();
            List<String> mapping = new ArrayList<String>(methods.length);
            for(Method method : methods) {
                Set<String> roleAuth = new HashSet<String>(roles);
                Set<String> authAuth = new HashSet<String>(auth);
                AuthMode methodMode = null;
                if((method.isAnnotationPresent(Authorization.class) && method.isAnnotationPresent(RequestMapping.class)) || classAuthAnno != null) {
                    if(log.isInfoEnabled()) {
                        log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                                "annotaion: " + method.getAnnotation(Authorization.class));
                    }
                    Authorization methodAuth = method.getAnnotation(Authorization.class);
                    if(methodAuth != null && methodAuth.roles().length > 0) {
                        roleAuth.addAll(Arrays.asList(methodAuth.roles()));
                    }
                    if(methodAuth != null && methodAuth.auth().length > 0) {
                        authAuth.addAll(Arrays.asList(methodAuth.auth()));
                    }
                    if(methodAuth != null) {
                        methodMode = methodAuth.mode();
                    }

                    List<String> methodUrls = null;

                    RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                    if(methodMapping == null) {
                        continue;
                    }
                    if(methodMapping.value().length > 0) {
                        methodUrls = Arrays.asList(methodMapping.value());
                        methodUrls = (List<String>)CollectionUtils.collect(methodUrls, new Transformer() {
                            @Override
                            public Object transform(Object o) {
                                if(o.toString().endsWith("/")) {
                                    return o.toString();
                                } else {
                                    return o.toString()+"/";
                                }
                            }
                        });
                    } else {
                        methodUrls = new ArrayList<String>();
                        methodUrls.add("/");
                    }
                    if((!CollectionUtils.isEmpty(roleAuth) || !CollectionUtils.isEmpty(authAuth)) && !methodUrls.isEmpty()) {
                        for(String methodUrl : methodUrls) {
                            for(String classUrl : classUrls) {
                                mapping.add(classUrl + methodUrl);
                                authInterceptor.addAuth(classUrl + methodUrl, new ArrayList<String>(roleAuth), new ArrayList<String>(authAuth), methodMode == null ? classMode : methodMode);
                            }
                        }
                    }
                } else {
                    if(log.isInfoEnabled()) {
                        log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                                "has no Authorization Annotaion");
                    }
                }
            }

            if(!mapping.isEmpty()) {
                if(log.isInfoEnabled()) {
                    log.info("class: " + bean.getClass() + ", mapping: " + mapping);
                }
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)beanFactory.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0");
                Field interceptorField = null;
                List<MappedInterceptor> mappedInterceptorList = null;
                try {
                    interceptorField = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("mappedInterceptors");
                    interceptorField.setAccessible(true);
                    mappedInterceptorList = (List<MappedInterceptor>)interceptorField.get(requestMappingHandlerMapping);
                    for(String classUrl : classUrls) {
                        mappedInterceptorList.add(new MappedInterceptor(new String[] {classUrl + "/**"}, authInterceptor));
                    }
                    ReflectionUtils.setField(interceptorField, requestMappingHandlerMapping, mappedInterceptorList);
                } catch (Exception e) {
                    log.error("RequestMappingHandlerMapping reflect field error", e);
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
