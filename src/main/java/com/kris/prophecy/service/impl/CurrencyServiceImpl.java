package com.kris.prophecy.service.impl;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.lock.RedisLock;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.mapper.VirtualCurrencyMapper;
import com.kris.prophecy.model.common.exception.CommonErrorCode;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.CurrencyService;
import com.kris.prophecy.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author by Kris on 9/26/2018.
 */
@Component
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VirtualCurrencyMapper virtualCurrencyMapper;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 账户充值
     */
    @Override
    public Response recharge(VirtualCurrency virtualCurrency) {
        RedisLock lock = new RedisLock(redisTemplate, "RECHARGE", 10000, 20000);
        try {
            if (lock.lock()) {
                //需要加锁的代码
                int ret1 = virtualCurrencyMapper.insertSelective(virtualCurrency);
                User user = new User();
                user.setBalance(virtualCurrency.getTransaction());
                user.setName(virtualCurrency.getAccountName());
                int ret2 = userMapper.updateByNameSelective(user);

                if (ret1 > 0 && ret2 > 0) {
                    return Response.message("充值成功");
                } else {
                    return Response.error(UserErrorCode.FAIL);
                }
            }
            return Response.error(CommonErrorCode.TOO_MANY_REQUEST);
        } catch (InterruptedException e) {
            LogUtil.logInfo("获取分布式锁失败");
            return Response.error(UserErrorCode.FAIL);
        } finally {
            /**
             *  为了让分布式锁的算法更稳键些，持有锁的客户端在解锁之前应该再检查一次自己的锁是否已经超时，再去做DEL操作，因为可能客户端因为某个耗时的操作而挂起，
             *  操作完的时候锁因为超时已经被别人获得，这时就不必解锁了。 ————这里没有做
             */
            lock.unlock();
        }
    }
}
