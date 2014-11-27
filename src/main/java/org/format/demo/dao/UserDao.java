package org.format.demo.dao;

import org.format.demo.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    public List<UserDto> searchByName(String userName);

}
