package org.format.demo.controller;

import org.format.demo.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class UserController {

    @RequestMapping
    public String index() {
        return "user";
    }

    @RequestMapping("/add")
    @Authorization(roles = {"admin"})
    public String add() {
        return "user";
    }

}
