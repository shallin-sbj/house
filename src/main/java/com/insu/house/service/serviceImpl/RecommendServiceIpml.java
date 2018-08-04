package com.insu.house.service.serviceImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.insu.house.common.model.House;
import com.insu.house.common.page.PageParams;
import com.insu.house.common.utils.RedisUtil;
import com.insu.house.service.HouseService;
import com.insu.house.service.RecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendServiceIpml implements RecommendService {

    private static final String HOT_HOUSE_KEY = "hot_house";

//    @Value("${redis.host}")
    private String ridisService = "120.79.130.20";

    private RedisUtil redisUtil = new RedisUtil();

    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Autowired
    private HouseService houseService;

    @Override
    public void increase(Long recommendId) {
        try {
            Jedis jedis = new Jedis(ridisService);
            jedis.zincrby(HOT_HOUSE_KEY, 1.0D, recommendId + "");
            jedis.zremrangeByRank(HOT_HOUSE_KEY, 0, -11);// 0代表第一个元素,-1代表最后一个元素，保留热度由低到高末尾10个房产
            jedis.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Long> getHot() {
        try {
            Jedis jedis = new Jedis(ridisService);
            Set<String> idSet = jedis.zrevrange(HOT_HOUSE_KEY, 0, -1);
            jedis.close();
            List<Long> ids = idSet.stream().map(Long::parseLong).collect(Collectors.toList());
            return ids;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);//有同学反应在未安装redis时会报500,在这里做下兼容,
            return Lists.newArrayList();
        }
    }

    @Override
    public List<House> getHotHouse(Integer recomSize) {
        House query = new House();
        List<Long> list = getHot();
        list = list.subList(0, Math.min(list.size(), recomSize));
        if (list.isEmpty()) {
            return Lists.newArrayList();
        }
        query.setIds(list);
        final List<Long> order = list;
        List<House> houses = houseService.queryAndSetImg(query, PageParams.build(recomSize, 1));
        Ordering<House> houseSort = Ordering.natural().onResultOf(hs -> {
            return order.indexOf(hs.getId());
        });
        return houseSort.sortedCopy(houses);
    }

    @Override
    public List<House> getLastest() {
        House query = new House();
        query.setSort("create_time");
        List<House> houses = houseService.queryAndSetImg(query, new PageParams(8, 1));
        return houses;
    }
}
