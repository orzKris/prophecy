package com.kris.prophecy.command;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.enums.DataFromEnum;
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
import org.springframework.context.annotation.Scope;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/4/22
 */
@Scope("prototype")
@Log4j2
public class RemoteHystrixCommand extends HystrixCommand<Result> {

    private static final int HTTP_STATUS = 200;

    private DispatchRequest dispatchRequest;

    public RemoteHystrixCommand(DispatchRequest dispatchRequest) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("remoteHystrixCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                        .withExecutionTimeoutInMilliseconds(1500)
                        .withCircuitBreakerRequestVolumeThreshold(20)
                        .withCircuitBreakerErrorThresholdPercentage(50)
                        .withCircuitBreakerSleepWindowInMilliseconds(5000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(50)
                        .withAllowMaximumSizeToDivergeFromCoreSize(true)
                        .withMaximumSize(1000)
                        .withMaxQueueSize(500)
                        .withQueueSizeRejectionThreshold(500)
                        .withKeepAliveTimeMinutes(5)));
        this.dispatchRequest = dispatchRequest;
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
                return new Result(DataErrorCode.DATASOURCE_ERROR);
            }
            responseBody = response.body().string();
            Result result = new Result(DataErrorCode.SUCCESS, new JSONObject(), DataFromEnum.DATA_FROM_DATASOURCE);
            result.setContentNotParsed(responseBody);
            return result;
        } catch (Exception e) {
            log.error("Error to call datasource, callId: {}", dispatchRequest.getCallId(), e);
            throw e;
        } finally {
            LogUtil.logInfo3rd(responseBody, start, dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        }
    }

    @Override
    protected Result getFallback() {
        LogUtil.logInfo3rdFallBack(dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        return new Result(DataErrorCode.FAIL);
    }

}
