package com.insu.house.service;

import com.insu.house.common.model.Blog;
import com.insu.house.common.page.PageData;
import com.insu.house.common.page.PageParams;

/**
 * blog服务
 */
public interface BlogService {

    PageData<Blog> queryBlog(Blog query, PageParams params);

    Blog queryOneBlog(int bolgId);
}
