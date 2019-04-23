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

    private DispatchRequest dispatchRequest;

    public RedisHystrixCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("redisHystrixCommand"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                        .withExecutionTimeoutInMilliseconds(1500)
                        //熔断器在整个统计时间内是否开启的阀值，默认20秒。也就是10秒钟内至少请求20次，熔断器才发挥起作用
                        .withCircuitBreakerRequestVolumeThreshold(20)
                        //默认:50%。当出错率超过50%后熔断器启动.
                        .withCircuitBreakerErrorThresholdPercentage(50)
                        //熔断器默认工作时间,默认:5秒.熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
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
            result = jedis.get(dispatchRequest.getKey());
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

    public void setDispatchRequest(DispatchRequest dispatchRequest) {
        this.dispatchRequest = dispatchRequest;
    }
}
