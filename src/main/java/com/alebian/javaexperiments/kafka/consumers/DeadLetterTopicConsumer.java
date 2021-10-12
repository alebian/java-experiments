package com.alebian.javaexperiments.kafka.consumers;

import com.alebian.javaexperiments.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        autoStartup = "true",
        groupId = Constants.Kafka.CONSUMER_GROUP,
        topics = "throwing.DLT"
)
@Slf4j
public class DeadLetterTopicConsumer {
    @KafkaHandler
    public void testHandler(String event) {
        log.info(event);
    }
}
