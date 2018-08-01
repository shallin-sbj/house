package com.insu.house.service;

import com.google.common.collect.Lists;
import com.insu.house.common.model.User;
import com.insu.house.common.utils.BeanHelper;
import com.insu.house.common.utils.HashUtils;
import com.insu.house.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insu.house.common.result.ResultMsg;

import java.util.List;

@Service
public class UserService {

    private final  Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileService fileService;

    @Autowired
    private MailService mailService;

    public List<User> getUsers() {
        return userMapper.selectUsers();
    }
    /**
     * 1. 插入数据库，非激活密码加密，头像展示保存到本地
     * 2. 生产key 绑定email
     * 3. 发送邮件给用户
     *
     * @param account
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addAccount(User account) {
        account.setPasswd(HashUtils.encryPassword(account.getPasswd()));
        logger.info("account.getAvatarFile(): " + account.getAvatarFile());
        List<String> imgList = fileService.getImgPaths(Lists.newArrayList(account.getAvatarFile()));
        logger.info("imgList: " + imgList.stream().toString());
        logger.info("imgList: " + imgList.stream().toString());
        if (!imgList.isEmpty()) {
            account.setAvatar(imgList.get(0));
        }
        BeanHelper.setDefaultProp(account, User.class);
        BeanHelper.onInsert(account);
        account.setEnable(0);
        userMapper.insert(account);
        mailService.registerNotify(account.getEmail());
        return true;
    }

    /**
     * 验证
     * @param key
     * @return
     */
    public boolean enable(String key) {
        return mailService.enable(key);
    }
}
