package com.kris.prophecy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Integer id;

    private String uid;

    private String type;

    private String title;

    private String content;

    private String target;

    private String publisherUid;

    private Integer flag;

    private Date createTime;

}