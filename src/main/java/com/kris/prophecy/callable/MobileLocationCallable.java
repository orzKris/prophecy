package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.MobileLocationConstant;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.framework.RedisService;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.AppendUrlUtil;
import com.kris.prophecy.utils.CommonCheckUtil;
import com.kris.prophecy.utils.KeyUtil;
import com.kris.prophecy.utils.LogUtil;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris
 * @date 2019/4/3
 */
@Component(ServiceCode.MOBILE_LOCATION)
@Scope("prototype")
@ConfigurationProperties(prefix = "mobile")
@Data
public class MobileLocationCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    private String url;

    private boolean isEnable = true;

    private int timeOut = 5000;

    @Autowired
    DispatchService dispatchService;

    @Autowired
    RedisService redisService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!CommonCheckUtil.validateMobile(paramJson.getString(MobileLocationConstant.MOBILE))) {
            return Result.fail("mobile invalid !");
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String mobile = paramJson.getString(MobileLocationConstant.MOBILE);
        String conditionMessage = String.format("mobile=%s", mobile);
        try {
            return getData(mobile);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "请求手机归属地接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String mobile) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(MobileLocationConstant.TEL, mobile);
        JSONObject keyString = new JSONObject();
        keyString.put(MobileLocationConstant.MOBILE, mobile);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .timeOut(timeOut)
                .isEnable(isEnable)
                .okHttpClient(new OkHttpClient())
                .callId(ServiceIdEnum.D005.getId())
                .requestParam(queryString)
                .request(getRequest(queryString))
                .key(KeyUtil.structureKey(keyString, ServiceIdEnum.D005.getId()))
                .build();
        Result result = dispatchService.dispatch(dispatchRequest, false);
        if (DataFromEnum.DATA_FROM_DATASOURCE == result.getDataFrom()) {
            return dealQueryResult(result, dispatchRequest);
        }
        return result;
    }

    private Request getRequest(JSONObject jsonObject) {
        String datasourceUrl = AppendUrlUtil.getURL(jsonObject, url);
        Request request = new Request.Builder().url(datasourceUrl).build();
        return request;
    }

    private Result dealQueryResult(Result result, DispatchRequest dispatchRequest) {
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(dispatchRequest.getRequestParam().getString(MobileLocationConstant.TEL), result.getContentNotParsed());
        result.setJsonResult(jsonResult);
        result.setContentNotParsed(null);
        redisService.asyncSet(dispatchRequest.getKey(), jsonResult.toJSONString(), 30 * 24 * 3600);
        return result;
    }
}
