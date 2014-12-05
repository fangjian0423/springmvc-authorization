package org.format.demo.postprocess;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.annotation.Authorization;
import org.format.demo.exception.AuthException;
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
public class AuthBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private static Log log = LogFactory.getLog(AuthBeanPostProcessor.class);
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        Authorization classAuthAnno = null;
        List<String> classUrls = new ArrayList<String>();
        if(bean.getClass().isAnnotationPresent(Controller.class)) { // 找出类中的Authorization注解
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

        if(!classUrls.isEmpty()) { // 类中的Authorization注解存在的话继续执行
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
            for(Method method : methods) { // 遍历带有Controller和RequestMapping注解的控制器
                Set<String> roleAuth = new HashSet<String>(roles);
                Set<String> authAuth = new HashSet<String>(auth);
                AuthMode methodMode = null;
                if((method.isAnnotationPresent(Authorization.class) && method.isAnnotationPresent(RequestMapping.class)) || classAuthAnno != null) {
                    if(log.isInfoEnabled()) {
                        log.info("class: " + bean.getClass() + ", method: " + method.getName() +
                                "annotaion: " + method.getAnnotation(Authorization.class));
                    }

                    RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                    if(methodMapping == null) {
                        continue;
                    }

                    Authorization methodAuth = method.getAnnotation(Authorization.class);

                    /** 整合类和方法的Authorization注解属性 */
                    if(methodAuth != null && methodAuth.roles().length > 0) {
                        roleAuth.addAll(Arrays.asList(methodAuth.roles()));
                    }
                    if(methodAuth != null && methodAuth.auth().length > 0) {
                        authAuth.addAll(Arrays.asList(methodAuth.auth()));
                    }
                    if(methodAuth != null) {
                        methodMode = methodAuth.mode();
                    }
                    /** 整合类和方法的Authorization注解属性 */

                    List<String> methodUrls = null;

                    if(methodMapping.value().length > 0) {
                        methodUrls = Arrays.asList(methodMapping.value());

                        // 处理url，所有不以“/”结尾的url全部加上url，然后丢到AuthInterceptor的静态变量中，同理AuthInterceptor中处理的时候不以"/"结尾的也加上"/"
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
                        // 不带value属性的RequestMapping默认为"/"
                        methodUrls = new ArrayList<String>();
                        methodUrls.add("/");
                    }
                    if((!CollectionUtils.isEmpty(roleAuth) || !CollectionUtils.isEmpty(authAuth)) && !methodUrls.isEmpty()) {
                        for(String methodUrl : methodUrls) {
                            for(String classUrl : classUrls) {
                                if(AuthInterceptor.getMappingInfoMap().containsKey(classUrl+methodUrl)) {
                                    throw new AuthException("exist url mapping: " + classUrl + methodUrl);
                                }
                                mapping.add(classUrl + methodUrl);
                                // 设置AuthInterceptor静态变量
                                AuthInterceptor.addAuth(classUrl + methodUrl, new ArrayList<String>(roleAuth), new ArrayList<String>(authAuth), methodMode == null ? classAuthAnno.mode() : methodMode);
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
                // 使用反射设置RequestMappingHandlerMapping的拦截器属性，加入AuthInterceptor
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)beanFactory.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0");
                Field interceptorField = null;
                List<MappedInterceptor> mappedInterceptorList = null;
                try {
                    interceptorField = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("mappedInterceptors");
                    interceptorField.setAccessible(true); // mappedInterceptors是个private final属性，需要加上这句话
                    mappedInterceptorList = (List<MappedInterceptor>)interceptorField.get(requestMappingHandlerMapping);
                    AuthInterceptor authInterceptor = new AuthInterceptor();
                    for(String classUrl : classUrls) {
                        mappedInterceptorList.add(new MappedInterceptor(new String[] {classUrl + "/**"}, authInterceptor));
                    }
                    ReflectionUtils.setField(interceptorField, requestMappingHandlerMapping, mappedInterceptorList);
                } catch (Exception e) {
                    log.error("RequestMappingHandlerMapping reflect field error", e);
                    throw new AuthException("reflect interceptors error", e);
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
