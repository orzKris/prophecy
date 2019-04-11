package com.kris.prophecy.service.impl;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.service.UserReflectService;
import com.kris.prophecy.utils.GetUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


/**
 * @author by Kris on 2018/12/12.
 */
@Component
public class UserReflectServiceImpl implements UserReflectService {

    @Autowired
    UserMapper userMapper;

    /**
     * 随机添加用户
     */
    @Override
    public List<User> addUser(long frequency) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < frequency; i++) {
            User user = new User();
            Random random = new Random();
            int index = random.nextInt(GetUserUtil.SUR_NAME.length - 1);
            String name = GetUserUtil.SUR_NAME[index];
            if (random.nextBoolean()) {
                name += GetUserUtil.getChinese() + GetUserUtil.getChinese();
            } else {
                name += GetUserUtil.getChinese();
            }
            user.setName(name);
            user.setAge((int) (Math.random() * 8 + 17));
            user.setUid(UUID.nameUUIDFromBytes(name.getBytes()).toString());
            user.setGraduationTime((int) (Math.random() * 8 + 2010));
            user.setPassword(GetUserUtil.genRandomNum());
            user.setSchool(GetUserUtil.GRADUATION_SCHOOL);
            String sex = null;
            random = new Random();
            if (random.nextBoolean()) {
                sex = GetUserUtil.MALE;
            } else {
                sex = GetUserUtil.FEMALE;
            }
            user.setSex(sex);
            index = random.nextInt(GetUserUtil.SUBORDINATE_CLASS.length - 1);
            String subordinateClass = GetUserUtil.SUBORDINATE_CLASS[index];
            user.setSubordinateClass(subordinateClass);
            users.add(user);
        }
        userMapper.insertUserList(users);
        return users;
    }
}
