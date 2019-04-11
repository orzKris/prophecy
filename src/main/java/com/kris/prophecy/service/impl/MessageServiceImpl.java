package com.kris.prophecy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.kris.prophecy.entity.Message;
import com.kris.prophecy.mapper.MessageMapper;
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
    public JSONObject sendMessage(Message message) {
        JSONObject jsonObject = new JSONObject();
        int ret = messageMapper.insertSelective(message);
        if (ret > 0) {
            jsonObject.put("response_msg", "通知发送成功");
        } else {
            jsonObject.put("response_msg", "通知发送失败");
        }
        return jsonObject;
    }

    @Override
    public JSONObject deleteMessage(Message message) {
        JSONObject jsonObject = new JSONObject();
        if (messageMapper.selectByPrimaryKey(message.getId()) == null
                || messageMapper.selectByPrimaryKey(message.getId()).getFlag() == 0) {
            jsonObject.put("response_msg", "该通知不存在");
            return jsonObject;
        }
        int ret = messageMapper.updateByPrimaryKeySelective(message);
        if (ret > 0) {
            jsonObject.put("response_msg", "通知删除成功");
        } else {
            jsonObject.put("response_msg", "删除通知失败");
        }
        return jsonObject;
    }

    @Override
    public PageData<Message> listMessage(Integer page, Integer pageSize, Integer readFlag, String uid) {
        PageHelper.startPage(page, pageSize);
        List<Message> messageList = messageMapper.listMessage(uid, readFlag);
        PageData<Message> messagePageData = new PageData<>(messageList, pageSize);
        return messagePageData;
    }

    @Override
    public JSONObject readMessage(Integer id, Integer flag, String uid) {
        JSONObject jsonObject = new JSONObject();
        if (flag == null) {
            List<Message> messageList = messageMapper.listMessage(uid, 0);
            if (messageList.isEmpty()) {
                jsonObject.put("response_msg", "没有未读通知");
                return jsonObject;
            }
            List<Integer> ids = new ArrayList<>();
            messageList.forEach(message -> {
                ids.add(message.getId());
            });
            int ret = messageMapper.readAll(ids, uid);
            if (ret > 0) {
                jsonObject.put("response_msg", "读取所有通知成功");
            } else {
                jsonObject.put("response_msg", "读取所有通知失败");
            }
        } else if (flag == 1) {
            if (messageMapper.selectByPrimaryKey(id) == null) {
                jsonObject.put("response_msg", "该通知不存在");
                return jsonObject;
            }
            int ret = messageMapper.insert(id, uid);
            if (ret > 0) {
                jsonObject.put("response_msg", "读取该条通知成功");
            } else {
                jsonObject.put("response_msg", "读取该条通知失败");
            }
        } else {
            jsonObject.put("response_msg", "参数flag错误");
        }
        return jsonObject;
    }
}
