package org.format.demo.controller;

import org.format.demo.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/dept")
@Controller
@Authorization(roles = {"admin"})
public class DeptController {

    @RequestMapping
    @Authorization(roles = {"manager"})
    public String index() {
        return "dept";
    }

    @RequestMapping("/add")
    @Authorization(auth = {"add_dept"})
    public String add() {
        return "dept";
    }

}
