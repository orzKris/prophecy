package com.kris.prophecy.service.impl;

import com.kris.prophecy.entity.User;
import com.kris.prophecy.enums.CommonConstant;
import com.kris.prophecy.mapper.AttentionMapper;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.AttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AttentionServiceImpl implements AttentionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AttentionMapper attentionMapper;

    @Override
    public boolean selectUid(String pid) {
        return userMapper.selectByUid(pid) != null;
    }

    @Override
    public Response attentionOperation(String uid, String pid, int flag) {
        DateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String time = dateFormat.format(new Date());
        int initFlag = attentionMapper.getAttentionStatus(uid, pid) == null ? 0 : attentionMapper.getAttentionStatus(uid, pid);
        if (initFlag == flag) {
            return Response.message("操作不合法！");
        }
        attentionMapper.insertAttentionStatistics(uid, pid, time, flag);
        attentionMapper.changeAttentionStatus(uid, pid, flag);
        if (flag == 1) {
            return Response.message("关注成功！");
        } else {
            return Response.message("取关成功！");
        }
    }

    @Override
    public List<User> getMyConcernedOrFans(String uid, int flag) {
        List<String> uidList;
        if (flag == 1) {
            uidList = attentionMapper.pidList(uid);
        } else {
            uidList = attentionMapper.uidList(uid);
        }
        if (uidList.size() == 0) {
            return new ArrayList<>();
        }
        return userMapper.listSelect(uidList);
    }

}
