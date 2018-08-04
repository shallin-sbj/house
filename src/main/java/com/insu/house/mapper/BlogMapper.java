package com.insu.house.mapper;

import com.insu.house.common.model.Blog;
import com.insu.house.common.page.PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogMapper {

    public List<Blog> selectBlog(@Param("blog")Blog query, @Param("pageParams")PageParams params);

    public Long selectBlogCount(Blog query);
}
