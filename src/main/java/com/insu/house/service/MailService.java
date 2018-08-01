package com.insu.house.service;

public interface MailService {

    void sendMail(String title, String url, String email);

    void registerNotify(String email);

    boolean enable(String key);
}
