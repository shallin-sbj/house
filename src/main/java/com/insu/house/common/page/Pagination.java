package com.insu.house.common.page;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * page 信息
 */
public class Pagination {

    private int pageNum;

    private int pageSize;

    private long totalCount;

    private List<Integer> pages = Lists.newArrayList();

    public Pagination() {
    }

    public Pagination(int pageSize,int pageNum,long totalCount) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        for (int i=0;i < pageNum;i++) {
            pages.add(i);
        }
        long pageCount = totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1);
        if (pageCount >pageNum) {
            for(int i= pageNum + 1; i<= pageCount ;i ++){
                pages.add(i);
            }
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }
}
