package com.kris.prophecy.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface AttentionMapper {

    @Insert("insert into attention (aid,pid,attention_time) values (#{uid},#{pid},#{attentionTime})")
    int insertAttention(@Param("uid") String uid, @Param("pid") String pid, @Param("attentionTime") String attentionTime);
}
