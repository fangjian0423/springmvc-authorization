package org.format.demo.dao;

import org.format.demo.entity.Auth;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthDao {

    public Auth searchByName(String authName);

}
