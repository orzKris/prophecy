package com.kris.prophecy.controller;

import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.CurrencyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author by Kris on 9/26/2018.
 */
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    private final static long MAX_TRANSACTION = 100 * 10000;

    /**
     * 账户充值
     */
    @PostMapping(value = "/recharge", produces = "application/json;charset=UTF-8")
    public Response recharge(@RequestBody VirtualCurrency virtualCurrency, @RequestHeader("uid") String uid) {
        if (StringUtils.isBlank(virtualCurrency.getAccountName()) || virtualCurrency.getTransaction() == null) {
            return Response.error(UserErrorCode.PARAM_ERROR);
        }
        if ((virtualCurrency.getTransaction() > MAX_TRANSACTION)) {
            return Response.error(UserErrorCode.CURRENCY_ERROR);
        }
        return currencyService.recharge(virtualCurrency);
    }
}
