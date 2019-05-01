package com.kris.prophecy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class InterfaceUsage {
    private Integer id;

    private String uid;

    private String uri;

    /**
     * 响应耗时
     */
    private long responseTime;

    private Date updateTime;

    private String args;

    private String status;

    private String failMessage;

}