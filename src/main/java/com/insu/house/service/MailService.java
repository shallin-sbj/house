package com.insu.house.service;

public interface MailService {

    /**
     * 发送邮件
     * @param title
     * @param url
     * @param email
     */
    void sendMail(String title, String url, String email);

    /**
     * 发送激活邮件
     * @param email
     */
    void registerNotify(String email);

    /**
     * 验证key值是否合法
     * @param key
     * @return
     */
    boolean enable(String key);

    /**
     *  发送重置密码邮件
     */
    void resetNotify(String email);

    /**
     * 根据可以获取email
     * @param key
     * @return
     */
    String getResetEmail(String key);

    /**
     * 验证可以有效性
     * @param key
     */
    void invalidateRestKey(String key);
}
