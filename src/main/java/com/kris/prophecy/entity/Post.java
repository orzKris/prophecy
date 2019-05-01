package com.kris.prophecy.entity;

import lombok.Data;

@Data
public class Post {

    /**
     * 帖子id
     */
    private Integer id;

    /**
     * 发帖人uid
     */
    private String uid;

    private String title;

    private String content;

    private String createTime;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 回复人uid
     */
    private String replyUid;

    /**
     * 回复人名称
     */
    private String replyName;

    /**
     * 回复内容
     */
    private String replyContent;

    private String  replyTime;

    /**
     * 被回复人姓名
     */
    private String respondentName;

    /**
     * 回复id
     */
    private Integer rid;

}