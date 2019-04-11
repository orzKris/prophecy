package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.entity.Message;
import com.kris.prophecy.service.MessageService;
import com.kris.prophecy.utils.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author by Kris on 2018/11/16.
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    /**
     * 通知发送接口
     * Param: title,content
     */
    @PostMapping("/post")
    public JSONObject notice(@RequestBody Message message, @RequestHeader("uid") String uid) {
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isBlank(message.getTitle()) || StringUtils.isBlank(message.getContent())) {
            jsonObject.put("response_msg", "缺少参数");
            return jsonObject;
        }
        message.setUid("0");
        message.setPublisherUid(uid);
        return messageService.sendMessage(message);
    }

    /**
     * 删除通知接口
     * Param: id
     */
    @GetMapping("/delete")
    public JSONObject delete(Message message, @RequestHeader("uid") String uid) {
        JSONObject jsonObject = new JSONObject();
        if (message.getId()==null){
            jsonObject.put("response_msg","缺少参数");
            return jsonObject;
        }
        return messageService.deleteMessage(message);
    }

    /**
     * 通知查询接口
     * Param: page,pageSize,readFlag(-NULL 所有消息，-0 未读消息，-1 已读消息）
     */
    @GetMapping("")
    public PageData<Message> listMessage(@RequestHeader("uid") String uid,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) Integer readFlag){
        pageSize = (pageSize == null || pageSize < 0 ? 5 : pageSize);
        page = (page == null || page < 1 ? 1 : page);
        return messageService.listMessage(page,pageSize,readFlag,uid);
    }

    /**
     * 读取通知接口
     * Param: id,flag(-1 读取单个消息 -NULL 读取所有消息)
     */
    @GetMapping("/read")
    public JSONObject readMessage(@RequestHeader("uid") String uid,
                                  @RequestParam(required = false) Integer id,
                                  @RequestParam(required = false) Integer flag){
        return messageService.readMessage(id,flag,uid);
    }
}
