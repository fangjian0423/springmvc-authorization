package org.format.demo.interceptor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
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
        String servletPath = request.getServletPath();
        if(!servletPath.endsWith("/")) {
            servletPath += "/";
        }
        if(mappingInfoMap.containsKey(servletPath)) {
            // 验证权限
            List<String> auths = mappingInfoMap.get(servletPath).getAuth();
            List<String> roles = mappingInfoMap.get(servletPath).getRoles();

//            ServletRequestAttributes reqAttr = (ServletRequestAttributes)(RequestContextHolder.getRequestAttributes());
//            String userName = (String)reqAttr.getRequest().getSession().getAttribute("LOGIN_NAME");

            if(roles.contains("admin")) {
                return true;
            } else {
                log.info("非admin用户，验证不通过");
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
