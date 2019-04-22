package com.kris.prophecy.command;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.enums.DataFromEnum;
import com.kris.prophecy.model.CallMap;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.LogUtil;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/4/22
 */
@Service
@Scope("prototype")
@Log4j2
public class DSHystrixCommand extends HystrixCommand<Result> {

    @Autowired
    private CallMap callMap;

    private static final int HTTP_STATUS = 200;

    private DispatchRequest dispatchRequest;

    protected DSHystrixCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("DSHystrixCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                        .withExecutionTimeoutInMilliseconds(1500)
                        .withCircuitBreakerRequestVolumeThreshold(100)
                        .withCircuitBreakerErrorThresholdPercentage(50)
                        .withCircuitBreakerSleepWindowInMilliseconds(5000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(50)
                        .withAllowMaximumSizeToDivergeFromCoreSize(true)
                        .withMaximumSize(1000)
                        .withMaxQueueSize(500)
                        .withQueueSizeRejectionThreshold(500)
                        .withKeepAliveTimeMinutes(5)));
    }

    @Override
    protected Result run() throws IOException {
        long start = System.currentTimeMillis();
        String responseBody = "";
        Request request = dispatchRequest.getRequest();
        Call call = dispatchRequest.getOkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            if (response.code() != HTTP_STATUS) {
                return new Result(callMap.getMap().get(dispatchRequest.getDsId()), DataErrorCode.DATASOURCE_ERROR);
            }
            responseBody = response.body().string();
            Result result = new Result(callMap.getMap().get(dispatchRequest.getDsId()), DataErrorCode.SUCCESS, new JSONObject(), DataFromEnum.DATA_FROM_DATASOURCE);
            result.setContentNotParsed(responseBody);
            return result;
        } finally {
            LogUtil.logInfo3rd(responseBody, start, dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        }
    }

    @Override
    protected Result getFallback() {
        LogUtil.logInfo3rdFallBack(dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        return new Result(DataErrorCode.FAIL);
    }

    public void setDispatchRequest(DispatchRequest dispatchRequest) {
        this.dispatchRequest = dispatchRequest;
    }

}
