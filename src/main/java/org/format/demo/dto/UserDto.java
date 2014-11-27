package org.format.demo.dto;

import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDto {

    private Long id;
    private String name;
    private List<RoleDto> roleList;
    private List<String> authList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleDto> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RoleDto> roleList) {
        this.roleList = roleList;
    }

    public List<String> getAuthList() {
        return authList;
    }

    public void setAuthList(List<String> authList) {
        this.authList = authList;
    }


    public Set<String> getAllAuth() {
        Set<String> allAuth = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(roleList)) {
            for(RoleDto roleDto : roleList) {
                allAuth.addAll(roleDto.getAuthList());
            }
        }
        if(CollectionUtils.isNotEmpty(authList)) {
            allAuth.addAll(authList);
        }
        return allAuth;
    }

}
