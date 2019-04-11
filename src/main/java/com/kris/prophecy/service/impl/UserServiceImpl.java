package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * @author by Kris on 8/26/2018.
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册功能
     */
    @Override
    public JSONObject register(User user) {
        if (userMapper.selectByName(user.getName()) != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("response_msg", "该用户已经注册");
            return jsonObject;
        }
        String uid = UUID.randomUUID() + "";
        user.setUid(uid);
        int ret = userMapper.insertSelective(user);
        JSONObject jsonObject = new JSONObject();
        if (ret > 0) {
            jsonObject.put("response_msg", "注册成功！");
        } else {
            jsonObject.put("response_msg", "注册失败！");
        }
        return jsonObject;
    }

    /**
     * 登录功能
     */
    @Override
    public JSONObject login(String name, String password) {
        JSONObject jsonObject = new JSONObject();
        if (userMapper.selectLogin(name, password) == null) {
            jsonObject.put("response_msg", "用户名或密码错误！");
            return jsonObject;
        }
        jsonObject.put("response_msg", "登陆成功，好久不见呀! " + name + "同学");
        return jsonObject;
    }

    /**
     * 用户信息查询
     */
    @Override
    public JSONObject getUser(String name) {
        JSONObject jsonObject = new JSONObject();
        if (userMapper.selectByName(name) == null) {
            jsonObject.put("response_msg", "该用户不存在！");
            return jsonObject;
        }
        User user = userMapper.selectByName(name);
        jsonObject.put("response_msg", user);
        return jsonObject;
    }

    /**
     * 校名年份查询用户信息
     */
    @Override
    public List<User> getSchool(User user, Pageable pageable) {
        List<User> userList = userMapper.selectList(user,pageable);
        return userList;
    }

    /**
     * 统计用户数量
     */
    @Override
    public Long getCount(User user) {
        return userMapper.selectCount(user);
    }

    /**
     * 用户信息更新
     */
    @Override
    public JSONObject userUpdate(User user, String uid) {
        user.setUid(uid);
        int ret = userMapper.updateByUidSelective(user);
        JSONObject jsonObject = new JSONObject();
        if (ret > 0) {
            jsonObject.put("response_msg", "用户信息更新成功！");
        } else {
            jsonObject.put("response_msg", "用户信息更新失败！");
        }
        return jsonObject;
    }
}
