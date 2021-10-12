package com.alebian.javaexperiments.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaTestController {
    private final KafkaTemplate<String, String> template;

    @PostMapping("/publish")
    public String publish(@RequestBody KafkaMessageDto dto) {
        log.info("Received message '{}' to publish in '{}'", dto.message, dto.topic);
        template.send(dto.topic, dto.message);
        return "Published message '" + dto.message + "' in topic '" + dto.topic + "'.";
    }

    private record KafkaMessageDto(String topic, String message) {}
}
