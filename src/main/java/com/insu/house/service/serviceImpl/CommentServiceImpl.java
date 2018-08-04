package com.insu.house.service.serviceImpl;

import com.insu.house.common.constants.CommonConstants;
import com.insu.house.common.model.Comment;
import com.insu.house.common.model.User;
import com.insu.house.common.utils.BeanHelper;
import com.insu.house.mapper.CommentMapper;
import com.insu.house.service.CommentService;
import com.insu.house.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @Override
    public void addHouseComment(Long houseId, String content, Long userId) {
        addComment(houseId,null, content, userId,1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(Long houseId, Integer blogId, String content, Long userId, int type) {
        Comment comment = new Comment();
        if (type == 1) {
            comment.setHouseId(houseId);
        }else {
            comment.setBlogId(blogId);
        }
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setType(type);
        BeanHelper.onInsert(comment);
        BeanHelper.setDefaultProp(comment, Comment.class);
        commentMapper.insert(comment);
    }

    @Override
    public void addBlogComment(int blogId, String content, Long userId) {
        addComment(null,blogId, content, userId, CommonConstants.COMMENT_BLOG_TYPE);
    }

    @Override
    public List<Comment> getHouseComments(long houseId, int size) {
        List<Comment> comments = commentMapper.selectComments(houseId,size);
        comments.forEach(comment -> {
            User user = userService.getUserById(comment.getUserId());
            comment.setAvatar(user.getAvatar());
            comment.setUserName(user.getName());
        });
        return comments;
    }

    @Override
    public List<Comment> getBlogComments(long blogId, int size) {
        List<Comment> comments = commentMapper.selectBlogComments(blogId,size);
        comments.forEach(comment -> {
            User user = userService.getUserById(comment.getUserId());
            comment.setUserName(user.getName());
            comment.setAvatar(user.getAvatar());
        });
        return comments;
    }
}
