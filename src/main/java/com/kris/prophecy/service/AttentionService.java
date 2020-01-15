package com.kris.prophecy.service;

import com.kris.prophecy.model.common.util.Response;

public interface AttentionService {

    /**
     * 判断被关注用户是否存在
     */
    boolean selectUid(String pid);

    /**
     * 关注用户操作
     */
    Response attentionOperation(String uid, String pid,int flag);

    /**
     * 获取我的关注列表
     */
    Response getMyConcerned(String uid);

    /**
     * 获取我的粉丝列表
     */
    Response getMyFans(String uid);

}
