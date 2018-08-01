package com.insu.house.controller;

import com.insu.house.common.model.User;
import com.insu.house.common.result.ResultMsg;
import com.insu.house.common.utils.UserHelper;
import com.insu.house.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/userlist")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    /**
     * 1. 注册验证
     * 2. 发送邮件
     * 3. 验证失败重定向到注册页面
     * @param user  为空时，为注册页
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/register")
    public String accountRegister(User user, ModelMap modelMap) {
        if (user == null || user.getName() == null) {
            return "/user/accounts/register";
        }
        // 用户验证
        ResultMsg resultMsg = UserHelper.validate(user);
        if (resultMsg.isSuccess()&& userService.addAccount(user)) {
            modelMap.put("email",user.getEmail());
            return "/user/accounts/registerSubmit";
        } else {
            return "redirect:/accounts/register?" + resultMsg.asUrlParams();
        }
    }

    @RequestMapping("accounts/verify")
    public String verify(String key) {
        boolean result = userService.enable(key);
        if (result) {
            return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
        } else {
            return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败,请确认链接是否过期");
        }
    }

}
