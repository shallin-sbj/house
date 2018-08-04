package com.insu.house.controller;

import com.insu.house.aop.SystemControllerAnnotation;
import com.insu.house.common.constants.CommonConstants;
import com.insu.house.common.model.User;
import com.insu.house.common.result.ResultMsg;
import com.insu.house.common.utils.HashUtils;
import com.insu.house.common.utils.PasswordUtils;
import com.insu.house.common.utils.UserHelper;
import com.insu.house.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    private PasswordUtils passwordUtils = new PasswordUtils();

    @SystemControllerAnnotation(description = "获取用户列表")
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
    @SystemControllerAnnotation(description = "用户注册")
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

    @SystemControllerAnnotation(description = "用户激活")
    @RequestMapping("accounts/verify")
    public String verify(String key) {
        boolean result = userService.enable(key);
        if (result) {
            return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
        } else {
            return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败,请确认链接是否过期");
        }
    }

    // --------------------- 登录页-------------------------
    /**
     * 登录接口
     */
    @SystemControllerAnnotation(description = "登录")
    @RequestMapping("/accounts/signin")
    public String signin(HttpServletRequest req) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String target = req.getParameter("target");
        if (username == null || password == null) {
            req.setAttribute("target", target);
            return "/user/accounts/signin";
        }
        User user = userService.auth(username, password);
        if (user == null) {
            return "redirect:/accounts/signin?" + "target=" + target + "&username=" + username + "&"
                    + ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
        } else {
            HttpSession session = req.getSession(true);
            session.setAttribute(CommonConstants.USER_ATTRIBUTE, user);
            // session.setAttribute(CommonConstants.PLAIN_USER_ATTRIBUTE, user);
            return StringUtils.isNoneBlank(target) ? "redirect:" + target : "redirect:/index";
        }
    }

    /**
     * 登出操作
     *
     * @param request
     * @return
     */
    @SystemControllerAnnotation(description = "退出")
    @RequestMapping("accounts/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.invalidate();
        return "redirect:/index";
    }

    // ---------------------个人信息页-------------------------
    /**
     * 1.能够提供页面信息 2.更新用户信息
     *
     * @param updateUser
     * @param model
     * @return
     */
    @SystemControllerAnnotation(description = "个人信息页")
    @RequestMapping("accounts/profile")
    public String profile(HttpServletRequest req, User updateUser, ModelMap model) {
        if (updateUser.getEmail() == null) {
            return "/user/accounts/profile";
        }
        userService.updateUser(updateUser, updateUser.getEmail());
        User query = new User();
        query.setEmail(updateUser.getEmail());
        List<User> users = userService.getUserByQuery(query);
        req.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE, users.get(0));
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }

    /**
     * 修改密码操作
     *
     * @param email
     * @param password
     * @param newPassword
     * @param confirmPassword
     * @param mode
     * @return
     */
    @SystemControllerAnnotation(description = "修改密码")
    @RequestMapping("accounts/changePassword")
    public String changePassword(String email, String password, String newPassword,
                                 String confirmPassword, ModelMap mode) {
        User user = userService.auth(email, password);
        if (user == null || !confirmPassword.equals(newPassword)) {
            return "redirct:/accounts/profile?" + ResultMsg.errorMsg("密码错误").asUrlParams();
        }
        User updateUser = new User();
        updateUser.setPasswd(passwordUtils.encryptedPassword(newPassword));
        userService.updateUser(updateUser, email);
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }


    /**
     * 忘记密码
     * @param username
     * @param modelMap
     * @return
     */
    @SystemControllerAnnotation(description = "忘记密码")
    @RequestMapping("accounts/remember")
    public String remember(String username, ModelMap modelMap) {
        if (StringUtils.isBlank(username)) {
            return "redirect:/accounts/signin?" + ResultMsg.errorMsg("邮箱不能为空").asUrlParams();
        }
        userService.resetNotify(username);
        modelMap.put("email", username);
        return "/user/accounts/remember";
    }

    @SystemControllerAnnotation(description = "过期的激活")
    @RequestMapping("accounts/reset")
    public String reset(String key,ModelMap modelMap){
        String email = userService.getResetEmail(key);
        if (StringUtils.isBlank(email)) {
            return "redirect:/accounts/signin?" + ResultMsg.errorMsg("重置链接已过期").asUrlParams();
        }
        modelMap.put("email", email);
        modelMap.put("success_key", key);
        return "/user/accounts/reset";
    }

    /**
     *
     * @param req
     * @param user
     * @return
     */
    @SystemControllerAnnotation(description = "重置密码")
    @RequestMapping(value="accounts/resetSubmit")
    public String resetSubmit(HttpServletRequest req,User user){
        ResultMsg retMsg = UserHelper.validateResetPassword(user.getKey(), user.getPasswd(), user.getConfirmPasswd());
        if (!retMsg.isSuccess() ) {
            String suffix = "";
            if (StringUtils.isNotBlank(user.getKey())) {
                suffix = "email=" + userService.getResetEmail(user.getKey()) + "&key=" +  user.getKey() + "&";
            }
            return "redirect:/accounts/reset?"+ suffix  + retMsg.asUrlParams();
        }
        User updatedUser =  userService.reset(user.getKey(),user.getPasswd());
        req.getSession(true).setAttribute(CommonConstants.USER_ATTRIBUTE, updatedUser);
        return "redirect:/index?" + retMsg.asUrlParams();
    }

}
