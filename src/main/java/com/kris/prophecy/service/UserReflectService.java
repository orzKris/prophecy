package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.User;

import java.util.List;

/**
 * @author by Kris on 2018/12/12.
 */
public interface UserReflectService {

    /**
     * 用户填充服务
     */
    List<JSONObject> addUser(long frequency);
}
