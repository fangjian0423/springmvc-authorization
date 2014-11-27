package org.format.demo.test.service;

import org.format.demo.dto.UserDto;
import org.format.demo.service.AuthService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:springConfig/applicationContext.xml")
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void testAuth() {
        UserDto userDto = authService.getUser("format");
        Assert.assertEquals("format", userDto.getName());
        Assert.assertEquals(2, userDto.getAuthList().size());
        Assert.assertEquals(1, userDto.getRoleList().size());
    }

}
