package com.kris.prophecy.entity;


import lombok.Data;

/**
 * @author bu Kris on 9/29/2018
 * 帖子概览模型
 */
@Data
public class PostOverview {

    private Integer id;

    private String  uid;

    private String title;

    private String content;

    private String  createTime;

    private Integer likes;

    /**
     * 回复总数量
     */
    private Integer count;

    /**
     * 发帖人姓名
     */
    private String name;

}
