package org.format.demo.interceptor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthInterceptor implements HandlerInterceptor {

    private static Log log = LogFactory.getLog(AuthInterceptor.class);

    private static Map<String, MappingInfo> mappingInfoMap = new HashMap<String, MappingInfo>();

    public synchronized void addAuth(String url, List<String> roles, List<String> auth) {
        mappingInfoMap.put(url, new MappingInfo(url, roles, auth));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        //TODO 拦截器验证权限
        log.info("a request coming");
        return false;
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

        public MappingInfo(String url, List<String> roles, List<String> auth) {
            this.url = url;
            this.roles = roles;
            this.auth = auth;
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
    }

}
