package com.kris.prophecy.lock;

import com.kris.prophecy.framework.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 首先，为了确保分布式锁可用，我们至少要确保锁的实现同时满足以下四个条件：
 * <p>
 * 互斥性。在任意时刻，只有一个客户端能持有锁。
 * 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
 * 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
 * 解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
 *
 * @author Kris
 * @date 2019/5/21
 */
@Component
public class JedisLock {

    @Autowired
    RedisService redisService;

    /**
     * 代码可能存在问题。可靠性待验证
     * lockName可以为共享变量名，也可以为方法名，主要是用于模拟锁信息
     */
    public boolean lock(String lockName) {
        //开始尝试加锁
        Long result = redisService.setnx(lockName, String.valueOf(System.currentTimeMillis() + 5000));
        if (result != null && result.intValue() == 1) {
            redisService.expire(lockName, 5);
            redisService.del(lockName);
            return true;
        } else {
            String lockValueA = redisService.get(lockName);
            if (lockValueA != null && Long.parseLong(lockValueA) >= System.currentTimeMillis()) {
                String lockValueB = redisService.getSet(lockName, String.valueOf(System.currentTimeMillis() + 5000));
                if (lockValueB == null || lockValueB.equals(lockValueA)) {
                    //加锁成功
                    redisService.expire(lockName, 5);
                    //执行业务逻辑
                    redisService.del(lockName);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
