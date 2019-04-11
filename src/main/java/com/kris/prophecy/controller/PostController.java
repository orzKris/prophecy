package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.kris.prophecy.entity.Post;
import com.kris.prophecy.entity.PostDetail;
import com.kris.prophecy.entity.PostOverview;
import com.kris.prophecy.service.PostService;
import com.kris.prophecy.utils.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author by Kris on 9/27/2018.
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    private final static Integer TITLE_MAX_SIZE = 15;

    /**
     * 发帖
     * Param: title,content
     */
    @PostMapping(value = "/add", produces = "application/json;charset=UTF-8")
    public JSONObject post(@RequestBody Post post, @RequestHeader("uid") String uid) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(post.getContent()) || StringUtils.isBlank(post.getTitle())) {
            jsonObject.put("response_msg", "缺少参数");
            return jsonObject;
        }
        if (post.getTitle().length() > TITLE_MAX_SIZE) {
            jsonObject.put("response_msg", "标题字数不能超过15字");
            return jsonObject;
        }
        post.setUid(uid);
        return postService.post(post);
    }

    /**
     * 回复帖子
     * Param: id,content,rid
     */
    @PostMapping(value = "/reply", produces = "application/json;charset=UTF-8")
    public JSONObject reply(@RequestBody Post post, @RequestHeader("uid") String uid) {
        JSONObject jsonObject = new JSONObject();
        if (post.getId() == null || StringUtils.isBlank(post.getContent())) {
            jsonObject.put("response_msg", "缺少参数");
            return jsonObject;
        }
        post.setReplyUid(uid);
        return postService.reply(post);
    }

    /**
     * 帖子概览列表
     * Param: title,content,start,end
     */
    @GetMapping(value = "/overview", produces = "application/json;charset=UTF-8")
    public PageData<PostOverview> overviewList(Post post, @RequestParam(required = false) String start,
                                               @RequestParam(required = false) String end,
                                               @RequestParam(required = false) Integer page,
                                               @RequestParam(required = false) Integer pageSize) {
        pageSize = (pageSize == null || pageSize < 0 ? 10 : pageSize);
        page = (page == null || page < 1 ? 1 : page);
        PageHelper.startPage(page, pageSize);
        List<PostOverview> postOverviews = postService.overviewList(post, start, end);
        PageData<PostOverview> postPageData = new PageData<>(postOverviews, pageSize);
        return postPageData;
    }

    /**
     * 帖子详情接口
     * Param: id
     */
    @GetMapping(value = "/detail", produces = "application/json;charset=UTF-8")
    public JSONObject detailList(@RequestParam("id") Integer id,
                                 @RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer pageSize) {
        pageSize = (pageSize == null || pageSize < 0 ? 5 : pageSize);
        page = (page == null || page < 1 ? 1 : page);
        PageHelper.startPage(page, pageSize);
        List<PostDetail> postDetails = postService.detailList(id);
        PageData<PostDetail> postPageData = new PageData<>(postDetails, pageSize);
        JSONObject jsonObject = postService.base(id);
        jsonObject.put("detail",postPageData);
        return jsonObject;
    }

    /**
     * 点赞接口
     * Param: id
     */
    @GetMapping(value = "/like", produces = "application/json;charset=UTF-8")
    public JSONObject like(@RequestParam("id") Integer id, @RequestHeader("uid") String uid) {
        return postService.like(id, uid);
    }
}

