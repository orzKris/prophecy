package com.kris.prophecy.service;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.model.common.util.Response;

public interface AttentionService {

    /**
     * 判断被关注用户是否存在
     */
    boolean selectUid(String pid);

    /**
     * 关注用户
     */
    Response insertAttention(String uid,String pid);


}
