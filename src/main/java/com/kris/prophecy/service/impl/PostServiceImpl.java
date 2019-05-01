package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Post;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.enums.CommonConstant;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.mapper.PostMapper;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.entity.PostDetail;
import com.kris.prophecy.entity.PostOverview;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author by Kris on 9/27/2018.
 */
@Component
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 帖子内容概览的最大字符长度
     */
    private final static Integer OVERVIEW_MAX_SIZE = 30;

    /**
     * 发帖
     */
    @Override
    public Response post(Post post) {
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        post.setCreateTime(dateFormat.format(new Date()));
        int ret = postMapper.insertSelective(post);
        if (ret > 0) {
            return Response.message("发帖成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    /**
     * 回复帖子
     */
    @Override
    public Response reply(Post post) {
        if (postMapper.selectByPrimaryKey(post.getId()) == null) {
            return Response.error(UserErrorCode.POST_NOT_EXIST);
        }
        if (post.getRid() != null && !postMapper.selectByRid(post.getRid()).equals(post.getId())) {
            return Response.error(UserErrorCode.POST_NOT_MATCH);
        }
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        post.setReplyTime(dateFormat.format(new Date()));
        int ret = postMapper.insertReply(post);
        if (ret > 0) {
            return Response.message("回复成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    /**
     * 帖子概览列表
     */
    @Override
    public List<PostOverview> overviewList(Post post, String start, String end) {
        List<PostOverview> posts = postMapper.selectOverview(post, start, end);
        posts.forEach(eachPost -> {
            eachPost.setCount(0);
            if (eachPost.getContent().length() > OVERVIEW_MAX_SIZE) {
                eachPost.setContent(eachPost.getContent().substring(0, 30) + "...");
            }
        });
        List<Integer> ids = new ArrayList<>();
        posts.forEach(eachPost -> {
            ids.add(eachPost.getId());
        });
        if (!ids.isEmpty()) {
            List<PostOverview> postOverviews = postMapper.selectCount(ids);
            posts.forEach(eachPost -> {
                postOverviews.forEach(postOverview -> {
                    if (postOverview.getId().equals(eachPost.getId())) {
                        eachPost.setCount(eachPost.getCount() + 1);
                    }
                });
            });
        }
        return posts;
    }

    /**
     * 帖子详情接口
     */
    @Override
    public List<PostDetail> detailList(Integer id) {
        List<PostDetail> postDetails = postMapper.selectDetail(id);
        List<Integer> rids = new ArrayList<>();
        postDetails.forEach(postDetail -> {
            if (postDetail.getRid() != null) {
                rids.add(postDetail.getRid());
            }
        });
        if (!rids.isEmpty()) {
            List<PostDetail> postDetailNames = postMapper.selectDetailName(rids);
            postDetails.forEach(postDetail -> {
                postDetailNames.forEach(postDetailName -> {
                    if (postDetailName.getId().equals(postDetail.getRid())) {
                        postDetail.setRespondentName(postDetailName.getReplyName());
                    }
                });
            });
        }
        return postDetails;
    }

    /**
     * 点赞接口
     */
    @Override
    public Response like(Integer id, String uid) {
        Post post = new Post();
        post.setId(id);
        if (postMapper.selectByPrimaryKey(id) == null) {
            return Response.error(UserErrorCode.POST_NOT_EXIST);
        }
        if (postMapper.getLikeFlag(uid, id) == null) {
            postMapper.insertFlag(uid, id);
            postMapper.plusByPrimaryKeySelective(post);
            return Response.message("点赞成功");

        } else if (postMapper.getLikeFlag(uid, id) == 0) {
            postMapper.updateLikeFlag(uid, id);
            postMapper.plusByPrimaryKeySelective(post);
            return Response.message("点赞成功");

        } else if (postMapper.getLikeFlag(uid, id) == 1) {
            postMapper.updateFlag(uid, id);
            postMapper.minusByPrimaryKeySelective(post);
            return Response.message("取消点赞");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    /**
     * 帖子基本信息获取
     */
    @Override
    public Response base(Integer id) {
        JSONObject jsonObject = new JSONObject();
        if (postMapper.selectByPrimaryKey(id) == null) {
            return Response.error(UserErrorCode.POST_NOT_EXIST);
        }
        Post post = postMapper.selectByPrimaryKey(id);
        String name = userMapper.selectByUid(post.getUid()).getName();
        jsonObject.put("title", post.getTitle());
        jsonObject.put("content", post.getContent());
        jsonObject.put("name", name);
        return Response.ok(jsonObject);
    }
}
