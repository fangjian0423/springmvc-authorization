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

    public List<UserDto> getUsers(String name) {
        List<UserDto> userDtoList = userDao.searchByName(name);
        for(UserDto userDto : userDtoList) {
            // 根据用户对应的角色
            List<Role> roleList = roleDao.getByUser(userDto.getName());
            List<RoleDto> roleDtoList = new ArrayList<RoleDto>(roleList.size());
            for(Role role : roleList) {
                roleDtoList.add(roleDao.searchByName(role.getName()));
            }
            userDto.setRoleList(roleDtoList);
        }
        return userDtoList;
    }

}
