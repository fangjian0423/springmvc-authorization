package org.format.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/login")
@Controller
public class LoginController {

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "login";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String login(@RequestParam("name")String name, HttpServletRequest request) {
        request.getSession().setAttribute("LOGIN_NAME", name);
        return "test";
    }

}
