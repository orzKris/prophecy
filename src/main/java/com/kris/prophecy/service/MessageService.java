package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Message;
import com.kris.prophecy.utils.PageData;



/**
 * @author by Kris on 2018/11/16.
 */
public interface MessageService {

    JSONObject sendMessage(Message message);

    JSONObject deleteMessage(Message message);

    PageData<Message> listMessage(Integer page, Integer pageSize, Integer readFlag, String uid);

    JSONObject readMessage(Integer id,Integer flag,String uid);
}
