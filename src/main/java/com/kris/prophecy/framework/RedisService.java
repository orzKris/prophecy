package com.kris.prophecy.framework;

/**
 * @author Kris
 * @date 2019/3/7
 */
public interface RedisService {

    String get(String key);

    void set(String key, String value, int seconds);

    void asyncSet(String key, String value, int seconds);
}
