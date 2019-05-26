package com.kris.prophecy.lock;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 实现的原理: Redis为单进程单线程模式，采用队列模式将并发访问变成串行访问，且多客户端对Redis的连接并不存在竞争关系。redis的SETNX命令可以方便的实现分布式锁。
 * 将 key 的值设为 value ，当且仅当 key 不存在。
 * 若给定的 key 已经存在，则SETNX不做任何动作。
 *
 * @author Kris
 * @date 2019/4/30
 */
@Log4j2
public class RedisLock {

    private RedisTemplate redisTemplate;

    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100;

    /**
     * Lock key path.
     */
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private int expireMsecs = 60 * 1000;

    /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMsecs = 10 * 1000;

    private volatile boolean locked = false;

    /**
     * Detailed constructor with default acquire timeout 10000 msecs and lock expiration of 60000 msecs.
     *
     * @param lockKey lock key (ex. account:1, ...)
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_LOCK";
    }

    /**
     * Detailed constructor with default lock expiration of 60000 msecs.
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * Detailed constructor.
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    /**
     * @return lock key
     */
    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisConnection connection) -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                byte[] data = connection.get(serializer.serialize(key));
                connection.close();
                return serializer.deserialize(data);
            });
        } catch (Exception e) {
            log.error("get redis error, key : {}", key);
        }
        return obj != null ? obj.toString() : null;
    }

    private boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisConnection connection) -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                connection.close();
                return success;
            });
        } catch (Exception e) {
            log.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }

    private String getSet(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisConnection connection) -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value));
                connection.close();
                return serializer.deserialize(ret);
            });
        } catch (Exception e) {
            log.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (String) obj : null;
    }

    /**
     * 获得 lock.
     * 实现思路: 主要是使用了redis 的setnx命令,缓存了锁.
     * redis缓存的key是锁的key,所有的共享, value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     *
     * @return true if lock is acquired, false acquire timeouted
     * @throws InterruptedException in case of thread interruption
     */
    public synchronized boolean lock() throws InterruptedException {
        int timeout = timeoutMsecs;
        while (timeout >= 0) {
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            //锁到期时间
            String expiresStr = String.valueOf(expires);
            if (this.setNX(lockKey, expiresStr)) {
                // lock acquired
                locked = true;
                return true;
            }

            //redis里的时间
            String currentValueStr = this.get(lockKey);
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                // lock is expired

                String oldValueStr = this.getSet(lockKey, expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //防止误删（覆盖，因为key是相同的）了他人的锁——这里达不到效果，这里值会被覆盖，但是因为什么相差了很少的时间，所以可以接受

                    //[分布式的情况下]:如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    // lock acquired
                    locked = true;
                    return true;
                }
            }
            timeout -= DEFAULT_ACQUIRE_RESOLUTION_MILLIS;

            /*
                延迟100 毫秒,  这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
                只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
                使用随机的等待时间可以一定程度上保证公平性
             */
            Thread.sleep(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);

        }
        return false;
    }

    /**
     * Acqurired lock release.
     */
    public synchronized void unlock() {
        if (locked) {
            redisTemplate.delete(lockKey);
            locked = false;
        }
    }

}
