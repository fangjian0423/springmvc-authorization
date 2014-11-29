package org.format.demo.interceptor;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.Configuration;
import org.format.demo.handler.AuthHandler;
import org.format.demo.model.AuthMode;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class AuthInterceptor implements HandlerInterceptor {

    private static Log log = LogFactory.getLog(AuthInterceptor.class);

    private static Map<String, MappingInfo> mappingInfoMap = new HashMap<String, MappingInfo>();

    public synchronized void addAuth(String url, List<String> roles, List<String> auth, AuthMode mode) {
        mappingInfoMap.put(url, new MappingInfo(url, roles, auth, mode));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String servletPath = request.getServletPath();
        if(!servletPath.endsWith("/")) {
            servletPath += "/";
        }
        if(mappingInfoMap.containsKey(servletPath)) {
            // 验证权限
            Set<String> auths = new HashSet<String>(mappingInfoMap.get(servletPath).getAuth());
            Set<String> roles = new HashSet<String>(mappingInfoMap.get(servletPath).getRoles());
            AuthMode mode = mappingInfoMap.get(servletPath).getMode();

            ServletRequestAttributes reqAttr = (ServletRequestAttributes)(RequestContextHolder.getRequestAttributes());
            String userName = (String)reqAttr.getRequest().getSession().getAttribute(Configuration.SESSION_LOGIN);
            if(StringUtils.isEmpty(userName)) {
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }

            AuthHandler authHandler = getAuthHandler();

            if(authHandler.handleAuth(userName, auths, roles, mode)) {
                authHandler.authSuccess();
                return true;
            } else {
                authHandler.authError();
                log.info("验证不通过");
                HandlerMethod handlerMethod = (HandlerMethod) obj;
                // json处理
                if(handlerMethod.getMethod().isAnnotationPresent(ResponseBody.class)) {
                    response.sendRedirect(request.getContextPath()+"/auth/noauth-body?success=false&msg=noauth");
                } else {
                    response.sendRedirect(request.getContextPath()+"/auth/noauth");
                }
                return false;
            }
        }
        return true;
    }

    private AuthHandler getAuthHandler() {
        Map<String, AuthHandler> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) Configuration.beanFactory, AuthHandler.class, true, false);
        if(matchingBeans == null || CollectionUtils.isEmpty(matchingBeans.values())) {
            throw new RuntimeException("no AuthHandler implememtation");
        }

        List<AuthHandler> authHandlers = new ArrayList<AuthHandler>(matchingBeans.values());
        OrderComparator.sort(authHandlers);


        AuthHandler authHandler = authHandlers.get(0);
        return authHandler;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception e) throws Exception {

    }


    private class MappingInfo {
        private String url;
        private List<String> roles;
        private List<String> auth;
        private AuthMode mode;

        public MappingInfo(String url, List<String> roles, List<String> auth, AuthMode mode) {
            this.url = url;
            this.roles = roles;
            this.auth = auth;
            this.mode = mode;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public List<String> getAuth() {
            return auth;
        }

        public void setAuth(List<String> auth) {
            this.auth = auth;
        }

        public AuthMode getMode() {
            return mode;
        }

        public void setMode(AuthMode mode) {
            this.mode = mode;
        }
    }

}
