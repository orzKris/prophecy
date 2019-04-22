package com.kris.prophecy.command;

import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.LogUtil;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Kris
 * @date 2019/4/22
 */
@Service
@Scope("prototype")
@Log4j2
public class RedisHystrixCommand extends HystrixCommand<Result> {

    @Autowired
    private JedisPool jedisPool;

    private String queryKey;

    private DispatchRequest dispatchRequest;

    public RedisHystrixCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("redisHystrixCommand"))
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
    protected Result run() {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        String result = "";
        try {
            jedis = jedisPool.getResource();
            result = jedis.get(this.queryKey);
            if (result == null) {
                return new Result(DataErrorCode.NO_CONTENT);
            }
            return new Result(DataErrorCode.SUCCESS, result);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            LogUtil.logInfoRedis(result, start, dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        }
    }

    @Override
    protected Result getFallback() {
        LogUtil.logInfoRedisFallBack(dispatchRequest.getRequestParam() == null ? "" : dispatchRequest.getRequestParam().toJSONString());
        return new Result(DataErrorCode.FAIL);
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public void setDispatchRequest(DispatchRequest dispatchRequest) {
        this.dispatchRequest = dispatchRequest;
    }
}
