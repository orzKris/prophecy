package com.kris.prophecy.framework.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.command.RemoteHystrixCommand;
import com.kris.prophecy.command.RedisHystrixCommand;
import com.kris.prophecy.config.ApplicationContextRegister;
import com.kris.prophecy.enums.DataFromEnum;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.framework.MongoService;
import com.kris.prophecy.model.DataCenter;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.CallMap;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.framework.DispatchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 调度服务
 *
 * @author Kris
 * @date 2019/2/1
 */
@Service
@Scope("prototype")
@Log4j2
@SuppressWarnings("all")
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private CallMap callMap;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private ApplicationContextRegister applicationContextRegister;

    /**
     * Redis最近一次重试时间
     */
    private long latestRetryTime = 0;

    /**
     * 存储熔断告警标示符
     */
    protected static final ConcurrentMap<String, Boolean> MAP = new ConcurrentHashMap<>();

    private final static long hystrixSleepWindowIntervalTime = 5000;

    @Override
    public Result dispatch(DispatchRequest dispatchRequest, boolean isParsed) throws IOException {
        RedisHystrixCommand redisHystrixCommand = getRedisHystrixCommand();
        redisHystrixCommand.setDispatchRequest(dispatchRequest);
        if (redisHystrixCommand.isCircuitBreakerOpen()) {
            redisCircuitBreakerOpenLog();
            if (System.currentTimeMillis() - latestRetryTime >= hystrixSleepWindowIntervalTime) {
                return retryRedisCommand(redisHystrixCommand, dispatchRequest, isParsed);
            }
            return dispatchDatasource(dispatchRequest, isParsed);
        } else {
            redisCircuitBreakerCloseLog();
            Result result = redisHystrixCommand.execute();
            if (DataErrorCode.SUCCESS.equals(result.getStatus())) {
                return new Result(callMap.getMap().get(dispatchRequest.getCallId()), result.getStatus(), JSONObject.parseObject(result.getContentNotParsed()), DataFromEnum.DATA_FROM_REDIS);
            } else {
                //调用数据源
                return dispatchDatasource(dispatchRequest, isParsed);
            }
        }
    }

    @Override
    public Result dispatchDatasource(DispatchRequest dispatchRequest, boolean isParsed) {
        if (!dispatchRequest.isEnable()) {
            return new Result(DataErrorCode.INTERFACE_FORBIDDEN);
        }
        long start = System.currentTimeMillis();
        Result result = new Result();
        RemoteHystrixCommand remoteHystrixCommand = new RemoteHystrixCommand(dispatchRequest);
        try {
            result = remoteHystrixCommand.execute();
            if (isParsed) {
                JSONObject jsonResult = JSONObject.parseObject(result.getContentNotParsed());
                result.setName(callMap.getMap().get(dispatchRequest.getCallId()));
                result.setJsonResult(jsonResult);
            } else {
                result.setName(callMap.getMap().get(dispatchRequest.getCallId()));
            }
            return result;
        } finally {
            mongoService.asyncInsert(new DataCenter(dispatchRequest.getKey(), result.getContentNotParsed()));
        }
    }

    private RedisHystrixCommand getRedisHystrixCommand() {
        return (RedisHystrixCommand) applicationContextRegister.getApplicationContext().getBean("redisHystrixCommand");
    }

    /**
     * Redis熔断告警日志，只在断路器开启时打印一次
     */
    private void redisCircuitBreakerOpenLog() {
        Boolean circuitBreakerOpenFlag = MAP.get("redisHystrixCommand");
        if (circuitBreakerOpenFlag == null || !circuitBreakerOpenFlag) {
            log.info("Redis CircuitBreaker opened");
            MAP.put("redisHystrixCommand", true);
            latestRetryTime = System.currentTimeMillis();
        }
    }

    /**
     * Redis熔断关闭告警日志，只在断路器关闭时打印一次
     */
    private void redisCircuitBreakerCloseLog() {
        Boolean circuitBreakerOpenFlag = MAP.get("redisHystrixCommand");
        if (circuitBreakerOpenFlag != null && circuitBreakerOpenFlag) {
            log.info("Redis CircuitBreaker close");
            MAP.put("redisHystrixCommand", false);
        }
    }

    private Result retryRedisCommand(RedisHystrixCommand redisHystrixCommand, DispatchRequest dispatchRequest, boolean isParsed) throws IOException {
        //熔断后，需要触发熔断器去重试
        Result result = redisHystrixCommand.execute();
        latestRetryTime = System.currentTimeMillis();
        if (DataErrorCode.SUCCESS.equals(result.getStatus())) {
            return new Result(callMap.getMap().get(dispatchRequest.getCallId()), result.getStatus(), JSONObject.parseObject(result.getContentNotParsed()), DataFromEnum.DATA_FROM_REDIS);
        } else {
            //调用数据源
            return dispatchDatasource(dispatchRequest, isParsed);
        }
    }
}
