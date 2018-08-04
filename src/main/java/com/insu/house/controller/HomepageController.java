package com.insu.house.controller;

import com.insu.house.aop.SystemControllerAnnotation;
import com.insu.house.common.model.House;
import com.insu.house.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 首页控制器
 */
@Controller
public class HomepageController {

    @Autowired
    private RecommendService recommendService;

    @SystemControllerAnnotation(description = "注册")
    @RequestMapping("index")
    public String accountsRegister(ModelMap modelMap) {
        List<House> houses = recommendService.getLastest();
        modelMap.put("recomHouses", houses);
        return "/homepage/index";
    }

    @RequestMapping("")
    public String index(ModelMap modelMap) {
        return "redirect:/index";
    }
}
