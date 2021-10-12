package com.alebian.javaexperiments.kafka.configuration;

import com.alebian.javaexperiments.kafka.configuration.ConsumerConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomDlqRecoverer implements ConsumerRecordRecoverer {
    private final KafkaTemplate<Object, Object> template;

    @SneakyThrows
    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception exception) {
        var exceptionClass = exception.getCause().getClass();
        var isRetryable = ConsumerConfiguration.EXCEPTION_SHOULD_RETRY.getOrDefault(exceptionClass, true);
        log.debug("Exception {} retryable: {}", exceptionClass, isRetryable);

        if (isRetryable) {
            throw exception.getCause();
        }

        Headers headers = new RecordHeaders(record.headers().toArray());
        enhanceHeaders(headers, record, exception);

        ProducerRecord<Object, Object> newDlqRecord = new ProducerRecord<>(record.topic() + ".DLT", null, record.key(), record.value(), headers);
        template.send(newDlqRecord);
    }

    /**
     * This method was taken almost identically from {@link DeadLetterPublishingRecoverer}
     */
    private void enhanceHeaders(Headers kafkaHeaders, ConsumerRecord<?, ?> record, Exception exception) {
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_ORIGINAL_TOPIC, record.topic().getBytes(StandardCharsets.UTF_8)));
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_ORIGINAL_PARTITION, ByteBuffer.allocate(Integer.BYTES).putInt(record.partition()).array()));
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_ORIGINAL_OFFSET, ByteBuffer.allocate(Long.BYTES).putLong(record.offset()).array()));
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_ORIGINAL_TIMESTAMP, ByteBuffer.allocate(Long.BYTES).putLong(record.timestamp()).array()));
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_ORIGINAL_TIMESTAMP_TYPE, record.timestampType().toString().getBytes(StandardCharsets.UTF_8)));
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_EXCEPTION_FQCN, exception.getClass().getName().getBytes(StandardCharsets.UTF_8)));

        String message = exception.getMessage();
        if (message != null) {
            kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE, exception.getMessage().getBytes(StandardCharsets.UTF_8)));
        }
        kafkaHeaders.add(new RecordHeader(KafkaHeaders.DLT_EXCEPTION_STACKTRACE, getStackTraceAsString(exception).getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * This method was taken identically from {@link DeadLetterPublishingRecoverer}
     */
    private String getStackTraceAsString(Throwable cause) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        cause.printStackTrace(printWriter);
        return stringWriter.getBuffer().toString();
    }
}
