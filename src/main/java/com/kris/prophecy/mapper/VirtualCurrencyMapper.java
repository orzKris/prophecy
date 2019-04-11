package com.kris.prophecy.mapper;

import com.kris.prophecy.entity.VirtualCurrency;

public interface VirtualCurrencyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VirtualCurrency record);

    /**
     * 账号充值
     */
    int insertSelective(VirtualCurrency record);

    VirtualCurrency selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VirtualCurrency record);

    int updateByPrimaryKey(VirtualCurrency record);
}