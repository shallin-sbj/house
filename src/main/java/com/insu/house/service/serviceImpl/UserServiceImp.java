package com.insu.house.service.serviceImpl;

import com.google.common.collect.Lists;
import com.insu.house.common.model.User;
import com.insu.house.common.utils.BeanHelper;
import com.insu.house.common.utils.HashUtils;
import com.insu.house.common.utils.PasswordUtils;
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

    private PasswordUtils passwordUtils = new PasswordUtils();

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
        logger.info("account info: getName: " + account.getName() +", getEmail: "+
                account.getEmail() +", getPasswd: "+  account.getPasswd());
        logger.info("encryptedPassword:" +  passwordUtils.encryptedPassword(account.getPasswd()));
        account.setPasswd(passwordUtils.encryptedPassword(account.getPasswd()));
        List<String> imgList = fileService.getImgPaths(Lists.newArrayList(account.getAvatarFile()));
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
        logger.info("username:" + username+ "password:" + password);
        logger.info("encryptedPassword:"+ passwordUtils.encryptedPassword(password));
        user.setPasswd(passwordUtils.encryptedPassword(password));
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
        updateUser.setEmail(email);
        BeanHelper.onUpdate(updateUser);
        userMapper.update(updateUser);
    }

    @Override
    public void resetNotify(String username) {
        mailService.resetNotify(username);
    }

    @Override
    public String getResetEmail(String key) {
        String email = "";
        try {
            email =  mailService.getResetEmail(key);
        } catch (Exception ignore) {
        }
        return email;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User reset(String key, String password) {
        String email = getResetEmail(key);
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setPasswd(passwordUtils.encryptedPassword(password));
        userMapper.update(updateUser);
        mailService.invalidateRestKey(key);
        return getUserByEmail(email);
    }

    private User getUserByEmail(String email) {
        User queryUser = new User();
        queryUser.setEmail(email);
        List<User> users = getUserByQuery(queryUser);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        User queryUser = new User();
        queryUser.setId(userId);
        List<User> users = getUserByQuery(queryUser);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }
}
