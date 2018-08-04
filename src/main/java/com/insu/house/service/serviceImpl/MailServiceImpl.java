package com.insu.house.service.serviceImpl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.insu.house.common.model.User;
import com.insu.house.mapper.UserMapper;
import com.insu.house.service.MailService;
import com.insu.house.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserMapper userMapper;

    @Value("${domain.name}")
    private String domainName;

    /**
     * 来源
     */
    @Value("${spring.mail.username}")
    private String from;

    private final Cache<String, String> registerCache =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(150, TimeUnit.MINUTES)
                    .removalListener(new RemovalListener<String, String>() {
                        @Override
                        public void onRemoval(RemovalNotification<String, String> notification) {
                            userMapper.delete(notification.getValue());
                        }
                    }).build();

    private final Cache<String, String> resetCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(15, TimeUnit.MINUTES).build();

    @Override
    @Async
    public void sendMail(String title, String url, String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(email);
        mailMessage.setText(url);
        try {
            mailSender.send(mailMessage);
        } catch (Exception e){
            logger.info("send errro,please dealWith!");
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void registerNotify(String email) {
        String randomKey = RandomStringUtils.randomAlphabetic(10);
        logger.info("registerNotify: email" + email+ ", key:" +  randomKey);
        registerCache.put(randomKey,email);
        String url = "http://" + domainName + "/accounts/verify?key=" + randomKey;
        logger.info("usr :" + url);
        sendMail("激活邮件",url,email);    //   发送邮件，暂时不发送
    }

    @Override
    public boolean enable(String key) {
        String email = registerCache.getIfPresent(key);
        if (StringUtils.isBlank(email)) {
            return  false;
        }
        User user = new User();
        user.setEmail(email);
        user.setEnable(1);
        userMapper.update(user);
        registerCache.invalidate(key);
        return true;
    }

    @Override
    @Async
    public void resetNotify(String email) {
        String randomKey = RandomStringUtils.randomAlphanumeric(10);
        resetCache.put(randomKey, email);
        String content = "http://" + domainName + "/accounts/reset?key=" + randomKey;
        sendMail("房产平台密码重置邮件", content, email);
    }

    @Override
    public String getResetEmail(String key) {
        return resetCache.getIfPresent(key);
    }

    @Override
    public void invalidateRestKey(String key) {
        resetCache.invalidate(key);
    }
}
