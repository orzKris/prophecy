package com.kris.prophecy.mapper;

import com.kris.prophecy.entity.InterfaceUsage;

public interface InterfaceUsageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InterfaceUsage record);

    int insertSelective(InterfaceUsage record);

    InterfaceUsage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InterfaceUsage record);

    int updateByPrimaryKey(InterfaceUsage record);
}