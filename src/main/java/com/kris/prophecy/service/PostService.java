package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Post;
import com.kris.prophecy.entity.PostDetail;
import com.kris.prophecy.entity.PostOverview;

import java.util.List;


/**
 * @author by Kris on 9/27/2018.
 */
public interface PostService {

    /**
     * 发帖
     */
    JSONObject post(Post post);

    /**
     * 回复帖子
     */
    JSONObject reply(Post post);

    /**
     * 帖子概览列表
     */
    List<PostOverview> overviewList(Post post, String start, String end);

    /**
     * 帖子详情接口
     */
    List<PostDetail> detailList(Integer id);

    /**
     * 点赞接口
     */
    JSONObject like(Integer id,String uid);

    /**
     * 获取帖子基本信息
     */
    JSONObject base(Integer id);
}
