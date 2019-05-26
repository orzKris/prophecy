package com.kris.prophecy.framework.impl;

import com.kris.prophecy.framework.RedisService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Kris
 * @date 2019/3/7
 */
@Service
@Log4j2
public class RedisServiceImpl implements RedisService {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 通过key获取储存在redis中的value
     */
    @Override
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            log.info("redis get value error");
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 同步新增key,并设置key 的生存时间 (以秒为单位)
     * key 存在 则更新
     */
    @Override
    public void set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            jedis.set(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.info("redis set value error");
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * key 不存在则 set,key 存在则不做任何动作
     */
    @Override
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.setnx(key, value);
            return result;
        } catch (Exception e) {
            log.info("redis setnx value error");
            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     */
    @Override
    public String getSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.getSet(key, value);
            return result;
        } catch (Exception e) {
            log.info("redis getSet value error");
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.expire(key, seconds);
            return result;
        } catch (Exception e) {
            log.info("redis expire value error");
            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long result = jedis.del(key);
            return result;
        } catch (Exception e) {
            log.info("redis del value error");
            return 0L;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 异步新增key,并设置key 的生存时间 (以秒为单位)
     * key 存在 则更新
     */
    @Override
    @HystrixCommand(fallbackMethod = "hystrixSet", threadPoolProperties = {@HystrixProperty(name = "coreSize", value = "50"),
            @HystrixProperty(name = "maximumSize", value = "200"), @HystrixProperty(name = "maxQueueSize", value = "100"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "100"), @HystrixProperty(name = "allowMaximumSizeToDivergeFromCoreSize", value = "true")})
    public void asyncSet(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            jedis.set(key, value);
            jedis.expire(key, seconds);
            log.info("Success to asyncInsert redis, key: {}, value: {}, seconds: {}", key, value, seconds);
        } finally {
            returnResource(jedis);
        }
    }

    private void hystrixSet(String key, String value, int seconds) {
        log.error("[INSERT_FALLBACK]: key: {}, value: {}, seconds: {}", key, value, seconds);
    }

    /**
     * 返还到连接池
     */
    private void returnResource(Jedis jedis) {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception e) {
                log.error("Fail to close jedis", e);
            }
        }
    }
}
