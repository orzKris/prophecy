package com.kris.prophecy.mapper;

import com.kris.prophecy.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author by Kris on 8/26/2018.
 */
@Component
public interface UserMapper {

    /**
     * 注册功能
     */
    int insertSelective(User user);

    /**
     * 注册检查和用户信息查询
     */
    User selectByName(@Param("name") String name);

    /**
     * uid查询用户姓名
     */
    User selectByUid(@Param("uid") String uid);

    /**
     * 登录功能
     */
    User selectLogin(@Param("name") String name, @Param("password") String password);

    /**
     * 列表查询
     */
    List<User> selectList(@Param("user") User user,@Param("pageable") Pageable pageable);

    /**
     * 统计用户数量
     */
    Long selectCount(User user);

    /**
     * 用户信息更新
     */
    int updateByUidSelective(@Param("user") User user);

    /**
     * 余额更新
     */
    int updateByNameSelective(@Param("user") User user);

    /**
     * 用户填充服务
     */
    void insertUserList(@Param("userList") List<User> userList);
}
