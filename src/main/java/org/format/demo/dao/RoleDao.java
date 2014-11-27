package org.format.demo.dao;

import org.format.demo.dto.RoleDto;
import org.format.demo.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao {

    public RoleDto searchByName(String roleName);

    public List<Role> getByUser(String userName);

}
