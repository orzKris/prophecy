package com.kris.prophecy.aspect;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.mapper.UserMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Kris
 * @date 2019/4/12
 */
@Aspect
@Component
public class CurrencyAspect {

    @Autowired
    private UserMapper userMapper;

    @Pointcut("execution (* com.kris.prophecy.controller.CurrencyController.*(..))")
    public void balanceExecution() {
    }

    /**
     * 接口调用统计
     */
    @Around("balanceExecution()")
    public JSONObject timeAround(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args;
        args = proceedingJoinPoint.getArgs();
        JSONObject response = new JSONObject();

        try {
            response = (JSONObject) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        VirtualCurrency virtualCurrency = (VirtualCurrency) args[0];
        String uid = (String) args[1];
        response.put("uid", uid);
        response.put("accountName", virtualCurrency.getAccountName());
        response.put("transaction", virtualCurrency.getTransaction());
        response.put("balance", userMapper.selectByUid(uid).getBalance());
        return response;
    }
}
