package org.format.demo.controller;


import org.format.demo.annotation.Authorization;
import org.format.demo.model.AuthMode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = {"/order", "/oldOrder"})
@Controller
public class OrderController {

    @RequestMapping
    @Authorization(roles = {"manager"})
    public String index() {
        return "user";
    }

    @RequestMapping("/add")
    @Authorization(auth = {"add-user", "add-order"}, mode = AuthMode.OR)
    public String add() {
        return "user";
    }

}
