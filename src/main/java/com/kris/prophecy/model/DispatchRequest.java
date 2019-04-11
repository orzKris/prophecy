package com.kris.prophecy.model;

import com.alibaba.fastjson.JSONObject;
import lombok.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 调用服务请求
 *
 * @author Kris
 * @date 2019/2/1
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DispatchRequest {

    /**
     * http请client对象
     */
    private OkHttpClient okHttpClient;

    /**
     * 数据源请求
     */
    private Request request;

    /**
     * 数据源编号
     */
    private String dsId;

    /**
     * 调用数据源的请求参数
     */
    private JSONObject requestParam;

    /**
     * 接口是否可用
     */
    private boolean isEnable = true;

    /**
     * 数据查询主键
     */
    private String key;

}
