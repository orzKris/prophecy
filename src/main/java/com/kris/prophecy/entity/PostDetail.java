package com.kris.prophecy.entity;

import lombok.Data;

/**
 * @author by Kris on 9/29/2018.
 * 帖子详情模型
 */
@Data
public class PostDetail {

    private Integer id;

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
