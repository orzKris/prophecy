package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.mapper.VirtualCurrencyMapper;
import com.kris.prophecy.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public JSONObject recharge(VirtualCurrency virtualCurrency) {
        int ret1 = virtualCurrencyMapper.insertSelective(virtualCurrency);
        User user = new User();
        user.setBalance(virtualCurrency.getTransaction());
        user.setName(virtualCurrency.getAccountName());
        int ret2 = userMapper.updateByNameSelective(user);
        JSONObject jsonObject = new JSONObject();
        if (ret1>0&&ret2>0){
            jsonObject.put("response_msg","充值成功");
        }
        else {
            jsonObject.put("response_msg","充值失败");
        }
        return jsonObject;
    }
}
