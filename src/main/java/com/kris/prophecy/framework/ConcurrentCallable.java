package com.kris.prophecy.framework;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.model.Result;

import java.util.concurrent.Callable;

/**
 * @author by Kris on 2018/12/21.
 */
public interface ConcurrentCallable extends Callable<Result> {

    /**
     * 初始化传参
     */
    void init(JSONObject paramJson);

    /**
     * 参数检查
     */
    Result checkParam(JSONObject paramJson);
}
