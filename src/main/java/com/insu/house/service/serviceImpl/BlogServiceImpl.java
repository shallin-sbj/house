package com.insu.house.service.serviceImpl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.insu.house.common.model.Blog;
import com.insu.house.common.page.PageData;
import com.insu.house.common.page.PageParams;
import com.insu.house.mapper.BlogMapper;
import com.insu.house.service.BlogService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public PageData<Blog> queryBlog(Blog query, PageParams params) {
        List<Blog> blogs =  blogMapper.selectBlog(query,params);
        populate(blogs);
        Long  count =  blogMapper.selectBlogCount(query);
        return PageData.<Blog>buildPage(blogs, count, params.getPageSize(), params.getPageNum());
    }

    private void populate(List<Blog> blogs) {
        if (!blogs.isEmpty()) {
            blogs.stream().forEach(item -> {
                String stripped =  Jsoup.parse(item.getContent()).text();
                item.setDigest(stripped.substring(0, Math.min(stripped.length(),40)));
                String tags = item.getTags();
                item.getTagList().addAll(Lists.newArrayList(Splitter.on(",").split(tags)));
            });
        }
    }
    @Override
    public Blog queryOneBlog(int bolgId) {
        Blog query = new Blog();
        query.setId(bolgId);
        List<Blog> blogs = blogMapper.selectBlog(query, new PageParams(1, 1));
        if (!blogs.isEmpty()) {
            return blogs.get(0);
        }
        return null;
    }
}
