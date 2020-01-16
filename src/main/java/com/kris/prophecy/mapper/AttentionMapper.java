package com.kris.prophecy.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AttentionMapper {

    @Insert("insert into attention_statistics (aid,pid,attention_time,attention_operation) values (#{uid},#{pid},#{attentionTime},#{attentionOperation})")
    int insertAttentionStatistics(@Param("uid") String uid, @Param("pid") String pid, @Param("attentionTime") String attentionTime, @Param("attentionOperation") int attentionOperation);

    @Select("select status from attention_status where aid = #{uid} and pid = #{pid}")
    Integer getAttentionStatus(@Param("uid") String uid, @Param("pid") String pid);

    @Insert("insert into attention_status (aid,pid,status) values (#{uid},#{pid},#{flag}) on duplicate key update status = values(status)")
    int changeAttentionStatus(@Param("uid") String uid, @Param("pid") String pid, @Param("flag") int flag);

    @Select("select pid from attention_status where aid = #{uid} and status = 1 order by update_time desc")
    List<String> pidList(@Param("uid") String uid);

    @Select("select uid from attention_status where pid = #{pid} and status = 1 order by update_time desc")
    List<String> uidList(@Param("pid") String pid);
}
