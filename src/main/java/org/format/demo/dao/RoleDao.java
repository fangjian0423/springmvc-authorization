package org.format.demo.dao;

import org.format.demo.dto.RoleDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao {

    public List<RoleDto> searchByName(String roleName);

}
