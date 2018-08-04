package com.insu.house.common.utils;

/**
 * 密码加密工具
 */
public class PasswordUtils {

    public String encryptedPassword(String password) {
        return HashUtils.encryPassword(password);
    }


}
