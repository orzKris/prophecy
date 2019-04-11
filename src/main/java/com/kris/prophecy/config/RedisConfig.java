package com.kris.prophecy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Kris
 * @date 2019/3/7
 */
@Configuration
@Order(1)
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfig {

    private String host;

    private int port;

    private int jedisPoolTimeout;

    private int jedisPoolMaxIdle;

    private String password;

    private boolean blockWhenExhausted;

    @Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(jedisPoolMaxIdle);
        jedisPoolConfig.setMaxWaitMillis(10000);
        // 连接耗尽时是否阻塞, false报异常, true阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        return new JedisPool(jedisPoolConfig, host, port, jedisPoolTimeout, password);
    }

}