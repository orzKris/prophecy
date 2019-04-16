package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.kris.prophecy.entity.Message;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.mapper.MessageMapper;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.MessageService;
import com.kris.prophecy.utils.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by Kris on 2018/11/16.
 */
@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Override
    public Response sendMessage(Message message) {
        int ret = messageMapper.insertSelective(message);
        if (ret > 0) {
            return Response.message("通知发送成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    @Override
    public Response deleteMessage(Message message) {
        if (messageMapper.selectByPrimaryKey(message.getId()) == null
                || messageMapper.selectByPrimaryKey(message.getId()).getFlag() == 0) {
            return Response.error(UserErrorCode.MESSAGE_NOT_EXIST);
        }
        int ret = messageMapper.updateByPrimaryKeySelective(message);
        if (ret > 0) {
            return Response.message("删除通知成功");
        } else {
            return Response.error(UserErrorCode.FAIL);
        }
    }

    @Override
    public Response listMessage(Integer page, Integer pageSize, Integer readFlag, String uid) {
        PageHelper.startPage(page, pageSize);
        List<Message> messageList = messageMapper.listMessage(uid, readFlag);
        PageData<Message> messagePageData = new PageData<>(messageList, pageSize);
        return Response.ok(messagePageData);
    }

    @Override
    public Response readMessage(Integer id, Integer flag, String uid) {
        JSONObject jsonObject = new JSONObject();
        if (flag == null) {
            List<Message> messageList = messageMapper.listMessage(uid, 0);
            if (messageList.isEmpty()) {
                return Response.message("没有未读通知");
            }
            List<Integer> ids = new ArrayList<>();
            messageList.forEach(message -> {
                ids.add(message.getId());
            });
            int ret = messageMapper.readAll(ids, uid);
            if (ret > 0) {
                return Response.message("读取所有通知成功");
            } else {
                return Response.error(UserErrorCode.FAIL);
            }
        } else if (flag == 1) {
            if (messageMapper.selectByPrimaryKey(id) == null) {
                jsonObject.put("response_msg", "该通知不存在");
                return Response.error(UserErrorCode.MESSAGE_NOT_EXIST);
            }
            int ret = messageMapper.insert(id, uid);
            if (ret > 0) {
                return Response.message("读取该条通知成功");
            } else {
                return Response.error(UserErrorCode.FAIL);
            }
        } else {
            return Response.error(UserErrorCode.PARAM_FLAG_ERROR);
        }
    }
}
