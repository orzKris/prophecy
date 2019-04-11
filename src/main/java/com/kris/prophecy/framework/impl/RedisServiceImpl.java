package com.kris.prophecy.framework.impl;

import com.kris.prophecy.framework.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 异步新增key,并设置key 的生存时间 (以秒为单位)
     * key 存在 则更新
     */
    @Override
    @Async
    public void asyncSet(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            jedis.set(key, value);
            jedis.expire(key, seconds);
            log.info("Success to asyncInsert redis, key: {}, value: {}, seconds: {}", key, value, seconds);
        } catch (Exception e) {
            log.info("redis asyncSet value error");
            e.printStackTrace();
        } finally {
            returnResource(jedis);
        }
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
                e.printStackTrace();
            }
        }
    }
}
