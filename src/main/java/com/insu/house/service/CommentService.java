package com.insu.house.service;

import com.insu.house.common.model.Comment;

import java.util.List;

/**
 * 评论服务
 */
public interface CommentService {

    void addHouseComment(Long houseId, String content, Long userId);

    void addComment(Long houseId,Integer blogId, String content, Long userId,int type);

    void addBlogComment(int blogId, String content, Long userId);

    List<Comment> getHouseComments(long houseId,int size);

    List<Comment> getBlogComments(long blogId, int size);

}
