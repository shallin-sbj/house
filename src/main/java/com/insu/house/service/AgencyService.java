package com.insu.house.service;

import com.insu.house.common.model.Agency;
import com.insu.house.common.model.User;
import com.insu.house.common.page.PageData;
import com.insu.house.common.page.PageParams;

import java.util.List;

/**
 * 经济人
 * （相当与中介）
 */
public interface AgencyService {
    /**
     * 访问user表获取详情 添加用户头像
     * @param userId
     * @return
     */
    User getAgentDeail(Long userId);

    PageData<User> getAllAgent(PageParams pageParams);

    Agency getAgency(Integer id);

    List<Agency> getAllAgency();

    int add(Agency agency);

    void sendMsg(User agent, String msg, String name, String email);
}
