package com.insu.house.controller;

import com.insu.house.aop.SystemControllerAnnotation;
import com.insu.house.common.constants.CommonConstants;
import com.insu.house.common.constants.HouseUserType;
import com.insu.house.common.interceptor.UserContext;
import com.insu.house.common.model.*;
import com.insu.house.common.page.PageData;
import com.insu.house.common.page.PageParams;
import com.insu.house.common.result.ResultMsg;
import com.insu.house.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * house相关
 */
@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private CityService cityService;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private CommentService commentService;

    /**
     * 支持:
     * 1.分页
     * 2.搜索功能
     * 3.支持图片，价格，标题，地址信息
     * @return
     */
    @SystemControllerAnnotation(description = "查询房屋列表")
    @RequestMapping("/house/list")
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap){
        PageData<House> ps = houseService.queryHouse(query, PageParams.build(pageSize, pageNum));
        modelMap.put("ps",ps);
        modelMap.put("vo",query);
        return "house/listing";
    }

    @SystemControllerAnnotation(description = "查询城市以及社区")
    @RequestMapping("/house/toAdd")
    public String toAdd(ModelMap modelMap) {
        modelMap.put("citys", cityService.getAllCitys());
        modelMap.put("communitys", houseService.getAllCommunitys());
        return "/house/add";
    }

    @SystemControllerAnnotation(description = "添加房屋信息")
    @RequestMapping("/house/add")
    public String doAdd(House house){
        User user = UserContext.getUser();
        house.setState(CommonConstants.HOUSE_STATE_UP);
        houseService.addHouse(house,user);
        return "redirect:/house/ownlist";
    }

    @SystemControllerAnnotation(description = "ownList")
    @RequestMapping("house/ownlist")
    public String ownlist(House house,Integer pageNum,Integer pageSize,ModelMap modelMap){
        User user = UserContext.getUser();
        house.setUserId(user.getId());
        house.setBookmarked(false);
        modelMap.put("ps", houseService.queryHouse(house, PageParams.build(pageSize, pageNum)));
        modelMap.put("pageType", "own");
        return "/house/ownlist";
    }

    /**PageParams
     * 查询房屋详情
     * 查询关联经纪人
     * @param id
     * @return
     */
    @SystemControllerAnnotation(description = "查询房屋详情")
    @RequestMapping("house/detail")
    public String houseDetail(Long id,ModelMap modelMap){
        House house = houseService.queryOneHouse(id);
        HouseUser houseUser = houseService.getHouseUser(id);
        recommendService.increase(id);
        List<Comment> comments = commentService.getHouseComments(id,8);
        if (houseUser.getUserId() != null && !houseUser.getUserId().equals(0)) {
            modelMap.put("agent", agencyService.getAgentDeail(houseUser.getUserId()));
        }
        List<House> rcHouses =  recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
        modelMap.put("recomHouses", rcHouses);
        modelMap.put("house", house);
        modelMap.put("commentList", comments);
        return "/house/detail";
    }

    @SystemControllerAnnotation(description = "添加留言")
    @RequestMapping("house/leaveMsg")
    public String houseMsg(UserMsg userMsg){
        houseService.addUserMsg(userMsg);
        return "redirect:/house/detail?id=" + userMsg.getHouseId() + ResultMsg.successMsg("留言成功").asUrlParams();
    }

    //1.评分
    @SystemControllerAnnotation(description = "评分")
    @ResponseBody
    @RequestMapping("house/rating")
    public ResultMsg houseRate(Double rating,Long id){
        houseService.updateRating(id,rating);
        return ResultMsg.successMsg("ok");
    }


    //2.收藏
    @SystemControllerAnnotation(description = "收藏")
    @ResponseBody
    @RequestMapping("house/bookmark")
    public ResultMsg bookmark(Long id){
        User user =	UserContext.getUser();
        houseService.bindUser2House(id, user.getId(), true);
        return ResultMsg.successMsg("ok");
    }

    //3.删除收藏
    @SystemControllerAnnotation(description = "删除收藏")
    @ResponseBody
    @RequestMapping("house/unbookmark")
    public ResultMsg unbookmark(Long id){
        User user =	UserContext.getUser();
        houseService.unbindUser2House(id,user.getId(),HouseUserType.BOOKMARK);
        return ResultMsg.successMsg("ok");
    }

    @SystemControllerAnnotation(description = "delsale")
    @RequestMapping(value="house/del")
    public String delsale(Long id,String pageType){
        User user = UserContext.getUser();
        houseService.unbindUser2House(id,user.getId(),pageType.equals("own")?HouseUserType.SALE:HouseUserType.BOOKMARK);
        return "redirect:/house/ownlist";
    }

    //4.收藏列表
    @SystemControllerAnnotation(description = "收藏列表")
    @RequestMapping("house/bookmarked")
    public String bookmarked(House house,Integer pageNum,Integer pageSize,ModelMap modelMap){
        User user = UserContext.getUser();
        house.setBookmarked(true);
        house.setUserId(user.getId());
        modelMap.put("ps", houseService.queryHouse(house, PageParams.build(pageSize, pageNum)));
        modelMap.put("pageType", "book");
        return "/house/ownlist";
    }
}

