package com.insu.house.service;

import com.insu.house.common.model.House;

import java.util.List;

/**
 * 推送-介绍服务
 */
public interface RecommendService {

    void increase(Long recommendId);

    List<Long> getHot();

    List<House> getHotHouse(Integer recomSize);

    List<House> getLastest();
}
