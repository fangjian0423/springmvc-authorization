package org.format.demo.dao;

import org.format.demo.dto.UserDto;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao {

    public UserDto searchByName(String userName);

}
