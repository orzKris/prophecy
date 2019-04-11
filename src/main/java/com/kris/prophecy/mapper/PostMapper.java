package com.kris.prophecy.mapper;

import com.kris.prophecy.entity.Post;
import com.kris.prophecy.entity.PostDetail;
import com.kris.prophecy.entity.PostOverview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Post record);

    /**
     * 发帖
     */
    int insertSelective(Post record);

    /**
     * 回复帖子
     */
    int insertReply(Post post);

    /**
     * 帖子概览查询
     */
    List<PostOverview> selectOverview(@Param("post") Post post, @Param("start") String start, @Param("end") String end);

    /**
     * 帖子总回复数统计
     */
    List<PostOverview> selectCount(@Param("ids") List<Integer> ids);

    /**
     * 单个帖子信息查询
     */
    Post selectByPrimaryKey(Integer id);

    /**
     * 帖子详情查询
     */
    List<PostDetail> selectDetail(Integer id);

    /**
     * 帖子回复人名称查询
     */
    List<PostDetail> selectDetailName(@Param("rids") List<Integer> rids);

    /**
     * 检查帖子与回复的匹配关系
     */
    Integer selectByRid(Integer rid);

    /**
     * 点赞
     */
    int plusByPrimaryKeySelective(Post record);

    /**
     * 取消赞
     */
    int minusByPrimaryKeySelective(Post record);

    /**
     * 查询点赞状态
     */
    @Select("select like_flag from like_statistics where uid=#{uid} and pid=#{pid}")
    Integer getLikeFlag(@Param("uid") String uid,@Param("pid") int pid);

    /**
     * 更新点赞状态
     */
    @Update("update like_statistics set like_flag=1 where uid=#{uid} and pid=#{pid}")
    void  updateLikeFlag(@Param("uid") String uid,@Param("pid") int pid);

    @Update("update like_statistics set like_flag=0 where uid=#{uid} and pid=#{pid}")
    void  updateFlag(@Param("uid") String uid,@Param("pid") int pid);

    /**
     * 插入点赞状态
     */
    @Insert("asyncInsert into like_statistics (like_flag,uid,pid) values (1,#{uid},#{pid})")
    void insertFlag(@Param("uid") String uid,@Param("pid") int pid);

    int updateByPrimaryKey(Post record);
}