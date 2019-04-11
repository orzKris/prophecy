package com.kris.prophecy.mapper;

import com.kris.prophecy.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    int deleteByPrimaryKey(Integer id);

    /**
     * 读取单个通知
     */
    int insert(@Param("id") Integer id,@Param("uid") String uid);

    /**
     * 通知发送接口
     */
    int insertSelective(Message record);

    /**
     * 单条消息检索
     */
    Message selectByPrimaryKey(Integer id);

    /**
     * 删除通知接口
     */
    int updateByPrimaryKeySelective(Message record);

    /**
     * 通知查询接口
     */
    List<Message> listMessage(@Param("uid") String  uid, @Param("readFlag") Integer readFlag);

    /**
     * 读取所有通知
     */
    int readAll(@Param("ids") List<Integer> ids,@Param("uid") String uid);

    int updateByPrimaryKey(List<Integer> ids);
}