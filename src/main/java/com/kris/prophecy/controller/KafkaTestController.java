package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.LocalErrorCode;
import com.kris.prophecy.model.Result;
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
    public Result sendKafka(HttpServletRequest request) {
        try {
            String message = request.getParameter("message");
            log.info("kafka的消息={}", message);
            kafkaTemplate.send("test", "key", message);
            log.info("发送kafka成功");
            Result result = new Result("kafka测试", LocalErrorCode.SUCCESS);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("response", message);
            result.setJsonResult(jsonObject);
            return result;
        } catch (Exception e) {
            log.error("发送kafka失败", e);
            Result result = new Result("kafka测试", LocalErrorCode.SUCCESS);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("response", "kafka测试失败");
            result.setJsonResult(jsonObject);
            return result;
        }
    }

}
