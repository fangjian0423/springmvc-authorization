package org.format.demo.handler;

import org.apache.commons.collections.CollectionUtils;
import org.format.demo.Configuration;
import org.format.demo.dao.AuthDao;
import org.format.demo.dao.RoleDao;
import org.format.demo.dto.RoleDto;
import org.format.demo.dto.UserDto;
import org.format.demo.exception.AuthException;
import org.format.demo.model.AuthMode;
import org.format.demo.service.AuthService;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultAuthHandler implements AuthHandler, Ordered {

    @Override
    public boolean handleAuth(String userName, Set<String> auths, Set<String> roles, AuthMode mode) {

        Set<String> allAuths = new HashSet<String>(auths);

        AuthService authService = (AuthService) Configuration.beanFactory.getBean("authService");
        UserDto userDto = authService.getUser(userName);

        if(userDto == null) {
            throw new AuthException("user: " + userName + " is not exist");
        }

        checkAuth(allAuths);

        if(CollectionUtils.isNotEmpty(roles)) {
            // 将角色转换成权限
            Set<String> roleAuths = transferRoleToAuth(roles);
            allAuths.addAll(roleAuths);
        }

        if(mode == AuthMode.AND && userDto.getAllAuth().containsAll(allAuths)) {
            return true;
        } else if(mode == AuthMode.OR) {
            for(String auth : auths) {
                if(userDto.getAllAuth().contains(allAuths)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkAuth(Set<String> allAuths) {
        AuthDao authDao = (AuthDao) Configuration.beanFactory.getBean("authDao");
        for(String auth : allAuths) {
            if(authDao.searchByName(auth) == null) {
                throw new AuthException("auth: " + auth + " is not exist");
            }
        }
    }

    @Override
    public void authSuccess() {

    }

    @Override
    public void authError() {

    }

    private Set<String> transferRoleToAuth(Set<String> roles) {
        RoleDao roleDao = (RoleDao) Configuration.beanFactory.getBean("roleDao");
        Set<String> result = new HashSet<String>();
        for(String role : roles) {
            RoleDto roleDto = roleDao.searchByName(role);
            if(roleDto == null) {
                throw new AuthException("not exist role: " + role);
            }
            result.addAll(roleDto.getAuthList());
        }
        return result;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
