package org.format.demo.controller;

import org.format.demo.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/store")
@Controller
@Authorization(roles = {"manager"})
public class StoreController {

    @RequestMapping
    public String index() {
        return "store";
    }

    @RequestMapping("/update")
    public String update() {
        return "store";
    }

}
