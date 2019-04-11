package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author by Kris on 8/26/2018.
 */
public interface UserService {

    /**
     * 注册
     */
    JSONObject register(User user);

    /**
     * 登录
     */
    JSONObject login(String name, String password);

    /**
     * 用户名查询用户信息
     */
    JSONObject getUser(String name);

    /**
     * 校名查询用户信息
     */
    List<User> getSchool(User user, Pageable pageable);

    /**
     * 统计用户数量
     */
    Long getCount(User user);

    /**
     * 用户信息更新
     */
    JSONObject userUpdate(User user, String uid);
}
