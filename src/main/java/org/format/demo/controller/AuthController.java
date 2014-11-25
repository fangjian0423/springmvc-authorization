package org.format.demo.controller;

import org.format.demo.model.CommonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/auth")
@Controller
public class AuthController {

    @RequestMapping("/noauth")
    public String noauth() {
        return "noauth";
    }

    @RequestMapping("/noauth-body")
    @ResponseBody
    public CommonData noauth(@RequestParam String msg, @RequestParam boolean success) {
        CommonData commonData = new CommonData(success, msg);
        return commonData;
    }

}
