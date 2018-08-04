package com.insu.house.common.page;
import com.insu.house.common.model.House;

import java.util.List;

/**
 * 页面数据
 */
public class PageData<T> {

    private List<T> list;

    private Pagination pagination;

    public PageData( Pagination pagination,List<T> list) {
        this.list = list;
        this.pagination = pagination;
    }

    public static  <T> PageData<T> buildPage(List<T> list,Long count,Integer pageSize,Integer pageNum){
        Pagination _pagination = new Pagination(pageSize, pageNum, count);
        return new PageData<>(_pagination, list);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
