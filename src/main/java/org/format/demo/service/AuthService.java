package org.format.demo.service;

import org.format.demo.dao.RoleDao;
import org.format.demo.dao.UserDao;
import org.format.demo.dto.RoleDto;
import org.format.demo.dto.UserDto;
import org.format.demo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;

    public UserDto getUser(String name) {
        UserDto userDto = userDao.searchByName(name);
        List<Role> roleList = roleDao.getByUser(userDto.getName());
        List<RoleDto> roleDtoList = new ArrayList<RoleDto>(roleList.size());
        for(Role role : roleList) {
            roleDtoList.add(roleDao.searchByName(role.getName()));
        }
        userDto.setRoleList(roleDtoList);
        return userDto;
    }

}
