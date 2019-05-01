package com.kris.prophecy.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VirtualCurrency {
    private Integer id;

    private String accountName;

    private Integer transaction;

    private Date updateTime;

}