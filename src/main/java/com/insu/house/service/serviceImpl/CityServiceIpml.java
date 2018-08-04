package com.insu.house.service.serviceImpl;

import com.google.common.collect.Lists;
import com.insu.house.common.model.City;
import com.insu.house.service.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceIpml implements CityService {

    @Override
    public List<City> getAllCitys() {
        City city = new City();
        city.setCityCode("110000");
        city.setCityName("北京");
        city.setId(1);
        return Lists.newArrayList(city);
    }
}
