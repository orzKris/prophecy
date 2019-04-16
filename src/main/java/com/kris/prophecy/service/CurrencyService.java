package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.model.common.util.Response;

/**
 * @author by Kris on 9/26/2018.
 */
public interface CurrencyService {

    /**
     * 账户充值
     */
    Response recharge(VirtualCurrency virtualCurrency);
}
