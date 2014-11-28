package org.format.demo.handler;

import org.apache.commons.collections.CollectionUtils;
import org.format.demo.Configuration;
import org.format.demo.dao.RoleDao;
import org.format.demo.dto.RoleDto;
import org.format.demo.dto.UserDto;
import org.format.demo.model.AuthMode;
import org.format.demo.service.AuthService;

import java.util.HashSet;
import java.util.Set;

public class DefaultAuthHandler implements AuthHandler {

    @Override
    public boolean handleAuth(String userName, Set<String> auths, Set<String> roles, AuthMode mode) {

        Set<String> allAuths = new HashSet<String>(auths);

        AuthService authService = (AuthService) Configuration.beanFactory.getBean("authService");
        UserDto userDto = authService.getUser(userName);

        if(userDto == null) {
            throw new RuntimeException("user: " + userName + " is not exist");
        }

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
                //FIXME
                throw new RuntimeException("not exist role: " + role);
            }
            result.addAll(roleDto.getAuthList());
        }
        return result;
    }

}
