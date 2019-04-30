package com.kris.prophecy.command;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.CommonConstant;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.enums.DataFromEnum;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.LogUtil;
import com.netflix.hystrix.*;
import lombok.extern.log4j.Log4j2;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Kris
 * @date 2019/4/22
 */
@Log4j2
public class RemoteHystrixCommand extends HystrixCommand<Result> {

    private static final int HTTP_STATUS = 200;

    private DispatchRequest dispatchRequest;

    public RemoteHystrixCommand(DispatchRequest dispatchRequest) {
        //命令分组用于对依赖操作分组,便于统计,汇总; CommandGroup是每个命令最少配置的必选参数，在不指定ThreadPoolKey的情况下，值用于对不同依赖的线程池/信号区分
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(CommonConstant.REMOTE_COMMAND_GROUP))
                //Command实例名称，如不声明区分该值，则Hystrix始终会实例化同一个对象，则之后设置的每个对象不同的CommandProperties无法生效
                .andCommandKey(HystrixCommandKey.Factory.asKey(CommonConstant.COMMAND_KEY_PREFIX + dispatchRequest.getCallId()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                        .withExecutionTimeoutInMilliseconds(dispatchRequest.getTimeOut())
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
