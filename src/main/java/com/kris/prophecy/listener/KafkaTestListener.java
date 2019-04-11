package com.kris.prophecy.listener;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * @author Kris
 * @date 2019/3/5
 */
@Log4j2
public class KafkaTestListener {

//    @KafkaListener(topics = {"test"})
    public void listen(ConsumerRecord<?, ?> record) {
        log.info("kafka的key: " + record.key());
        log.info("kafka的value: " + record.value().toString());
    }
}
