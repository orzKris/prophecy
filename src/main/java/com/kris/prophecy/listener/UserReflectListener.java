package com.kris.prophecy.listener;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.reflect.annotation.InvokeListener;
import com.kris.prophecy.reflect.annotation.ServiceDataMapping;
import com.kris.prophecy.enums.ServiceCode;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.service.UserReflectService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author by Kris on 2018/12/12.
 */
@InvokeListener
public class UserReflectListener {

    @Autowired
    private UserReflectService userReflectService;

    @ServiceDataMapping(ServiceCode.ADD_USER_REFLECT)
    public List<JSONObject> addUser(long frequency) {
        return userReflectService.addUser(frequency);
    }
}
