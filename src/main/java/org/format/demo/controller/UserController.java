package org.format.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class UserController {

    public UserController() {
        System.out.println("user");
    }

    @RequestMapping
    public String index() {
        return "user";
    }

}
