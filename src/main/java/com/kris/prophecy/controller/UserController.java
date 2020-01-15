package com.kris.prophecy.controller;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author by Kris on 8/26/2018.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     */
    @PostMapping(value = "/reg", produces = "application/json;charset=UTF-8")
    public Response register(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getGraduationTime()) || StringUtils.isEmpty(user.getName())
                || StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getSex()) || StringUtils.isEmpty(user.getSchool())
                || StringUtils.isEmpty(user.getSubordinateClass())) {
            return Response.error(UserErrorCode.PARAM_ERROR);
        }
        return userService.register(user);
    }

    /**
     * 登录
     */
    @GetMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public Response login(@RequestParam("name") String name, @RequestParam("password") String password) {
        return userService.login(name, password);
    }

    /**
     * 用户信息查询
     */
    @GetMapping(value = "", produces = "application/json;charset=UTF-8")
    public Response getUser(@RequestParam("name") String name) {
        return userService.getUser(name);
    }

    /**
     * 条件查询用户信息
     */
    @GetMapping(value = "/list", produces = "application/json;charset=UTF-8")
    public Response getUsers(User user, Pageable pageable) {
        List<User> userList = userService.getUsers(user, pageable);
        Long count = userService.getCount(user);
        Page<User> results = new PageImpl<>(userList, pageable, count);
        return Response.ok(results);
    }

    /**
     * 用户信息更新
     */
    @PostMapping(value = "/update", produces = "application/json;charset=UTF-8")
    public Response updateUser(@RequestBody User user, @RequestHeader("uid") String uid) {
        return userService.userUpdate(user, uid);
    }
}
