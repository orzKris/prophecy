package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.service.CurrencyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by Kris on 9/26/2018.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    private final static long MAX_TRANSACTION = 100*10000;

    /**
     * 账户充值
     */
    @PostMapping(value = "/recharge", produces = "application/json;charset=UTF-8")
    public JSONObject recharge(@RequestBody VirtualCurrency virtualCurrency){
        JSONObject jsonObject=new JSONObject();
        if (StringUtils.isBlank(virtualCurrency.getAccountName())||virtualCurrency.getTransaction()==null){
            jsonObject.put("response_msg","缺少参数");
            return jsonObject;
        }
        if ((virtualCurrency.getTransaction()>MAX_TRANSACTION)){
            jsonObject.put("response_msg","最大充值金额为1000000");
            return jsonObject;
        }
        return currencyService.recharge(virtualCurrency);
    }
}
