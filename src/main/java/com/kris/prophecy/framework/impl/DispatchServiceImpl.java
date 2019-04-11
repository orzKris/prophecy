package com.kris.prophecy.framework.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.DataFromEnum;
import com.kris.prophecy.enums.ResponseConstant;
import com.kris.prophecy.framework.MongoService;
import com.kris.prophecy.model.DataCenter;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.CallMap;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.framework.RedisService;
import com.kris.prophecy.utils.LogUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 调度服务
 *
 * @author Kris
 * @date 2019/2/1
 */
@Service
@Scope("prototype")
@Log4j2
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private CallMap callMap;

    @Autowired
    RedisService redisService;

    @Autowired
    MongoService mongoService;

    private static final int HTTP_STATUS = 200;

    @Override
    public Result dispatch(DispatchRequest dispatchRequest, boolean isParsed) throws IOException {
        Result result = this.dispatchCache(dispatchRequest);
        if (ResponseConstant.FAIL.equals(result.getStatus()) || result.getJsonResult() == null) {
            result = this.dispatchDatasource(dispatchRequest, isParsed);
        }
        return result;
    }

    @Override
    public Result dispatchDatasource(DispatchRequest dispatchRequest, boolean isParsed) throws IOException {
        if (!dispatchRequest.isEnable()) {
            return new Result(ResponseConstant.INTERFACE_FORBIDDEN);
        }
        String responseBody = "";
        long start = System.currentTimeMillis();
        Result result;
        try {
            Request request = dispatchRequest.getRequest();
            Call call = dispatchRequest.getOkHttpClient().newCall(request);
            Response response = call.execute();
            if (response.code() != HTTP_STATUS) {
                return new Result(callMap.getMap().get(dispatchRequest.getDsId()), ResponseConstant.DATASOURCE_ERROR);
            }
            responseBody = response.body().string();
            if (isParsed) {
                JSONObject jsonResult = JSONObject.parseObject(responseBody);
                result = new Result(callMap.getMap().get(dispatchRequest.getDsId()), ResponseConstant.SUCCESS, jsonResult, DataFromEnum.DATA_FROM_DATASOURCE);
            } else {
                result = new Result(callMap.getMap().get(dispatchRequest.getDsId()), ResponseConstant.SUCCESS, new JSONObject(), DataFromEnum.DATA_FROM_DATASOURCE);
                result.setContentNotParsed(responseBody);
            }
            return result;
        } finally {
            LogUtil.logInfo3rd("调度服务", responseBody, start, dispatchRequest.getRequestParam().toJSONString());
            mongoService.asyncInsert(new DataCenter(dispatchRequest.getKey(), responseBody));
        }
    }

    @Override
    public Result dispatchCache(DispatchRequest dispatchRequest) {
        String responseBody = "";
        long start = System.currentTimeMillis();
        JSONObject jsonResult = new JSONObject();
        try {
            jsonResult = JSON.parseObject(redisService.get(dispatchRequest.getKey()));
            Result result = new Result(callMap.getMap().get(dispatchRequest.getDsId()), ResponseConstant.SUCCESS, jsonResult, DataFromEnum.DATA_FROM_REDIS);
            return result;
        } catch (Exception e) {
            return new Result(callMap.getMap().get(dispatchRequest.getDsId()), ResponseConstant.FAIL, jsonResult, DataFromEnum.DATA_FROM_REDIS);
        } finally {
            LogUtil.logInfoRedis("调度服务", responseBody, start, dispatchRequest.getRequestParam().toJSONString());
        }
    }
}
