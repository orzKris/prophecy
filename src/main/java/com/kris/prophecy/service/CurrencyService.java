package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.VirtualCurrency;

/**
 * @author by Kris on 9/26/2018.
 */
public interface CurrencyService {

    /**
     * 账户充值
     */
    JSONObject recharge(VirtualCurrency virtualCurrency);
}
