package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.common.util.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kris
 * @date 2019/3/5
 */
@RestController
@RequestMapping("/kafka")
@Log4j2
public class KafkaTestController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public Response sendKafka(HttpServletRequest request) {
        try {
            String message = request.getParameter("message");
            log.info("kafka的消息={}", message);
            kafkaTemplate.send("test", "key", message);
            log.info("发送kafka成功");
            return Response.message("kafka测试成功");
        } catch (Exception e) {
            log.error("发送kafka失败", e);
            return Response.message("kafka测试失败");
        }
    }

}
