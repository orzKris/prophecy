package com.kris.prophecy.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 数据来源
 *
 * @author Kris
 * @date 2019/3/11
 */
public enum DataFromEnum implements IEnum<Integer> {
    DATA_FROM_NO(0, "未从任何源查询数据"),
    DATA_FROM_REDIS(1, "数据来源于Redis缓存"),
    DATA_FROM_MONGO(2, "数据来源于Mongo缓存"),
    DATA_FROM_DATASOURCE(3, "数据来源于数据源"),
    DATA_FROM_MULTI_SOURCE(4, "数据来源于数据源和缓存,适用于一次调用查询多次源或缓存的接口"),
    DATA_FROM_MYSQL(6, "从mysql获取数据");

    private Integer value;

    private String desc;

    private DataFromEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
