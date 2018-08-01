package com.insu.house.service.serviceImpl;

import com.google.common.collect.Lists;
import com.insu.house.common.model.User;
import com.insu.house.common.utils.BeanHelper;
import com.insu.house.common.utils.HashUtils;
import com.insu.house.mapper.UserMapper;
import com.insu.house.service.FileService;
import com.insu.house.service.MailService;
import com.insu.house.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImp  implements UserService {

    private final  Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    /**
     * 图像路径配置
     */
    @Value("${file.prefix}")
    private String imgPrefix;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private MailService mailService;

    @Override
    public List<User> getUsers() {
        return userMapper.selectUsers();
    }
    @Override
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
    @Override
    public boolean enable(String key) {
        return mailService.enable(key);
    }

    @Override
    public User auth(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPasswd(HashUtils.encryPassword(password));
        user.setEnable(1);
        List<User> list = getUserByQuery(user);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<User> getUserByQuery(User user) {
        List<User> list = userMapper.selectUsersByQuery(user);
        list.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
        return list;
    }
    @Override
    public void updateUser(User updateUser, String email) {

    }

    @Override
    public void resetNotify(String username) {

    }

    @Override
    public String getResetEmail(String key) {
        return null;
    }

    @Override
    public User reset(String key, String passwd) {
        return null;
    }
}
