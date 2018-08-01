package com.insu.house.controller;

import com.insu.house.common.model.User;
import com.insu.house.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
@Controller
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("hi")
    public String hellWorld() {
        return new Date() + "     hello world ";
    }

    @RequestMapping("hello")
    public String  hello(ModelMap modelMap){
        List<User> users = userService.getUsers();
        User one = users.get(0);
        modelMap.put("user", one);
        return "hello";
    }

    @RequestMapping("index")
    public String index() {
        return "homepage/index";
    }

}
