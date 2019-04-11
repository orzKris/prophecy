package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Post;
import com.kris.prophecy.mapper.PostMapper;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.entity.PostDetail;
import com.kris.prophecy.entity.PostOverview;
import com.kris.prophecy.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public PostServiceImpl(PostMapper postMapper, UserMapper userMapper) {
        this.postMapper = postMapper;
        this.userMapper = userMapper;
    }

    /**
     * 帖子内容概览的最大字符长度
     */
    private final static Integer OVERVIEW_MAX_SIZE = 30;

    /**
     * 发帖
     */
    @Override
    public JSONObject post(Post post) {
        int ret = postMapper.insertSelective(post);
        JSONObject jsonObject = new JSONObject();
        if (ret > 0) {
            jsonObject.put("response_msg", "发帖成功");
        } else {
            jsonObject.put("response_msg", "发帖失败");
        }
        return jsonObject;
    }

    /**
     * 回复帖子
     */
    @Override
    public JSONObject reply(Post post) {
        JSONObject jsonObject = new JSONObject();
        if (postMapper.selectByPrimaryKey(post.getId()) == null) {
            jsonObject.put("response_msg", "该帖子不存在");
            return jsonObject;
        }
        if (post.getRid() != null && !postMapper.selectByRid(post.getRid()).equals(post.getId())) {
            jsonObject.put("response_msg", "帖子与回复不匹配");
            return jsonObject;
        }
        int ret = postMapper.insertReply(post);
        if (ret > 0) {
            jsonObject.put("response_msg", "回复成功");
        } else {
            jsonObject.put("response_msg", "回复失败");
        }
        return jsonObject;
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
    public JSONObject like(Integer id, String uid) {
        JSONObject jsonObject = new JSONObject();
        Post post = new Post();
        post.setId(id);
        if (postMapper.selectByPrimaryKey(id) == null) {
            jsonObject.put("response_msg", "该帖子不存在");
            return jsonObject;
        }
        if (postMapper.getLikeFlag(uid, id) == null) {
            postMapper.insertFlag(uid, id);
            postMapper.plusByPrimaryKeySelective(post);
            jsonObject.put("response_msg", "点赞成功");
        } else if (postMapper.getLikeFlag(uid, id) == 0) {
            postMapper.updateLikeFlag(uid, id);
            postMapper.plusByPrimaryKeySelective(post);
            jsonObject.put("response_msg", "点赞成功");
        } else if (postMapper.getLikeFlag(uid, id) == 1) {
            postMapper.updateFlag(uid, id);
            postMapper.minusByPrimaryKeySelective(post);
            jsonObject.put("response_msg", "取消点赞");
        }
        return jsonObject;
    }

    /**
     * 帖子基本信息获取
     */
    @Override
    public JSONObject base(Integer id) {
        JSONObject jsonObject = new JSONObject();
        if (postMapper.selectByPrimaryKey(id) == null) {
            jsonObject.put("response_msg", "该帖子不存在");
            return jsonObject;
        }
        Post post = postMapper.selectByPrimaryKey(id);
        String name = userMapper.selectByUid(post.getUid()).getName();
        jsonObject.put("title", post.getTitle());
        jsonObject.put("content", post.getContent());
        jsonObject.put("name", name);
        return jsonObject;
    }
}
