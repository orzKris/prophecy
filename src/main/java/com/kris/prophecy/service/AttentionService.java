package com.kris.prophecy.service;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.model.common.util.Response;

import java.util.List;

public interface AttentionService {

    /**
     * 判断被关注用户是否存在
     */
    boolean selectUid(String pid);

    /**
     * 关注用户操作
     */
    Response attentionOperation(String uid, String pid, int flag);

    /**
     * 获取我的关注列表或粉丝列表
     */
    List<User> getMyConcernedOrFans(String uid, int flag);

}
