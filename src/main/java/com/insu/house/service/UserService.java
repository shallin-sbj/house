package com.insu.house.service;

import com.insu.house.common.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    /**
     * 1. 插入数据库，非激活密码加密，头像展示保存到本地
     * 2. 生产key 绑定email
     * 3. 发送邮件给用户
     * @param account
     * @return
     */
    boolean addAccount(User account);

    /**
     * 验证
     * @param key
     * @return
     */
    boolean enable(String key);

    /**
     *  验证用户密码
     * @param username
     * @param password
     * @return
     */
    User auth(String username, String password);

    void updateUser(User updateUser, String email);

    List<User> getUserByQuery(User query);

    /**
     * 发送重置密码邮件
     * @param email
     */
    void resetNotify(String email);

    /**
     * 根据Key获取email
     * @param key
     * @return
     */
    String getResetEmail(String key);

    /**
     * 重置密码操作
     * @param key
     * @param passwd
     * @return
     */
    User reset(String key, String passwd);

    User getUserById(Long userId);
}
