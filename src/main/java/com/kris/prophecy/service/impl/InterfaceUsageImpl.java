package com.kris.prophecy.service.impl;

import com.kris.prophecy.entity.InterfaceUsage;
import com.kris.prophecy.mapper.InterfaceUsageMapper;
import com.kris.prophecy.service.InterfaceUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Kris
 * @date 2019/2/1
 */
@Component
public class InterfaceUsageImpl implements InterfaceUsageService {

    @Autowired
    InterfaceUsageMapper interfaceUsageMapper;

    @Override
    public void insertInterfaceUsage(long start, String uri, String args, String uid, String status) {
        InterfaceUsage interfaceUsage = new InterfaceUsage();
        interfaceUsage.setResponseTime(System.currentTimeMillis() - start);
        interfaceUsage.setUri(uri);
        interfaceUsage.setArgs(args);
        interfaceUsage.setUid(uid);
        interfaceUsage.setStatus(status);
        interfaceUsageMapper.insertSelective(interfaceUsage);
    }
}
