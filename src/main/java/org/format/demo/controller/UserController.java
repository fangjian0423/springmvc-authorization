package org.format.demo.controller;

import org.format.demo.annotation.Authorization;
import org.format.demo.model.CommonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/user")
@Controller
public class UserController {

    @RequestMapping
    public String index() {
        return "user";
    }

    @RequestMapping("/update")
    @ResponseBody
    @Authorization(roles = {"manager"})
    public String update() {
        return "user";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public CommonData delete() {
        CommonData data = new CommonData(true, "success");
        return data;
    }

    @RequestMapping("/batchDelete")
    @Authorization(roles = {"admin"})
    @ResponseBody
    public CommonData batchDelete() {
        CommonData data = new CommonData(true, "success");
        return data;
    }

    @RequestMapping("/batchUpdate")
    @Authorization(auth = "update-user")
    @ResponseBody
    public CommonData batchUpdate() {
        CommonData data = new CommonData(true, "success");
        return data;
    }

    @RequestMapping("/add")
    @Authorization(roles = {"manager"})
    public String add() {
        return "user";
    }

}
