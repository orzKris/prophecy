package com.kris.prophecy.aspect;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.VirtualCurrency;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.model.common.util.Response;
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
    public Response timeAround(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args;
        args = proceedingJoinPoint.getArgs();
        Response response = new Response();

        try {
            response = (Response) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        VirtualCurrency virtualCurrency = (VirtualCurrency) args[0];
        String uid = (String) args[1];

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", uid);
        jsonObject.put("accountName", virtualCurrency.getAccountName());
        jsonObject.put("transaction", virtualCurrency.getTransaction());
        jsonObject.put("balance", userMapper.selectByUid(uid).getBalance());
        response = new Response<>(response.getResponseCode(), response.getMessage(), jsonObject);
        return response;
    }
}
