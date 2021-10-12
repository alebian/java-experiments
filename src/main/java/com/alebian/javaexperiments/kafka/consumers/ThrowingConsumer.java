package com.alebian.javaexperiments.kafka.consumers;

import com.alebian.javaexperiments.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        autoStartup = "true",
        containerFactory = Constants.Kafka.CUSTOM_DLQ_LISTENER_FACTORY,
        groupId = Constants.Kafka.CONSUMER_GROUP,
        topics = "throwing"
)
@Slf4j
public class ThrowingConsumer {
    @KafkaHandler
    public void throwingHandler(String event) {
        log.info(event);
        throw new NullPointerException("Let DLT handler take this one");
    }
}
