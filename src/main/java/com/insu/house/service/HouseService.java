package com.insu.house.service;

import com.insu.house.common.constants.HouseUserType;
import com.insu.house.common.model.*;
import com.insu.house.common.page.PageData;
import com.insu.house.common.page.PageParams;

import java.util.List;

public interface HouseService {

    /**
     * 1.查询小区
     * 2.图片服务器地址
     * 3.构建分页结果
     * 4.分页主键
     *
     * @param query
     * @param pageParams
     */
    PageData<House> queryHouse(House query, PageParams pageParams);

    /**
     * 查询所有社区
     * @return
     */
    List<Community> getAllCommunitys();

    /**
     * 添加房屋图片
     * 添加户型图片
     * 插入房产信息
     * 绑定用户到房产的关系
     * @param house
     * @param user
     */
    void addHouse(House house, User user);

    /**
     * 查询房子
     * @param id
     * @return
     */
    House queryOneHouse(Long id);

    /**
     * 获取户主信息
     * @param houseUserId
     * @return
     */
    HouseUser getHouseUser(Long houseUserId);

    /**
     *  添加用户信息
     * @param userMsg
     */
    void addUserMsg(UserMsg userMsg);

    /**
     * 添加等级
     * @param id
     * @param rating
     */
    void updateRating(Long id, Double rating);

    /**
     *  绑定用户和房子
     */
    void bindUser2House(Long houseId, Long userId, boolean collect);
    /**
     *  解除绑定用户和房子
     *
     */
    void unbindUser2House(Long id, Long userId, HouseUserType type);

    /**
     *
     * @param query
     * @param pageParams
     * @return
     */
    List<House> queryAndSetImg(House query, PageParams pageParams);
}
