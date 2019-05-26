package com.kris.prophecy.framework;

/**
 * @author Kris
 * @date 2019/3/7
 */
public interface RedisService {

    String get(String key);

    void set(String key, String value, int seconds);

    Long setnx(String key, String value);

    String getSet(String key, String value);

    Long expire(String key, int seconds);

    Long del(String key);

    void asyncSet(String key, String value, int seconds);
}
