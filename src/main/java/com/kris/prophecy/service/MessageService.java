package com.kris.prophecy.service;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Message;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.utils.PageData;



/**
 * @author by Kris on 2018/11/16.
 */
public interface MessageService {

    Response sendMessage(Message message);

    Response deleteMessage(Message message);

    Response listMessage(Integer page, Integer pageSize, Integer readFlag, String uid);

    Response readMessage(Integer id,Integer flag,String uid);
}
