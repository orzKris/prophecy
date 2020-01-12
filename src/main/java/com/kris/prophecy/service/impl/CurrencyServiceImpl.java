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

    /**
     * 账户充值
     */
    @Override
    public Response recharge(VirtualCurrency virtualCurrency) {
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
}
