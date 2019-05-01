package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.enums.CommonConstant;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public Response register(User user) {
        if (userMapper.selectByName(user.getName()) != null) {
            return Response.error(UserErrorCode.USER_ALREADY_EXIST);
        }
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        user.setRegisterTime(dateFormat.format(new Date()));
        String uid = UUID.nameUUIDFromBytes(user.getName().getBytes()).toString();
        user.setUid(uid);
        int ret = userMapper.insertSelective(user);
        if (ret > 0) {
            return Response.message("注册成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    /**
     * 登录功能
     */
    @Override
    public Response login(String name, String password) {
        if (userMapper.selectLogin(name, password) == null) {
            return Response.error(UserErrorCode.LOGIN_ERROR);
        }
        return Response.message("登陆成功");
    }

    /**
     * 用户信息查询
     */
    @Override
    public Response getUser(String name) {
        if (userMapper.selectByName(name) == null) {
            return Response.error(UserErrorCode.USER_NOT_EXIST);
        }
        User user = userMapper.selectByName(name);
        return Response.ok(user);
    }

    /**
     * 校名年份查询用户信息
     */
    @Override
    public List<User> getSchool(User user, Pageable pageable) {
        List<User> userList = userMapper.selectList(user, pageable);
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
    public Response userUpdate(User user, String uid) {
        user.setUid(uid);
        int ret = userMapper.updateByUidSelective(user);
        if (ret > 0) {
            return Response.message("用户信息更新成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }
}
