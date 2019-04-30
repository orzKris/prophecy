package com.kris.prophecy.service.impl;

import com.kris.prophecy.entity.InterfaceUsage;
import com.kris.prophecy.mapper.InterfaceUsageMapper;
import com.kris.prophecy.service.InterfaceUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Kris
 * @date 2019/2/1
 */
@Component
public class InterfaceUsageImpl implements InterfaceUsageService {

    @Autowired
    InterfaceUsageMapper interfaceUsageMapper;

    /**
     * 如果方法加了@Transactional注解，那么这个方法抛出异常，就会回滚，数据库里面的数据也会回滚。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
