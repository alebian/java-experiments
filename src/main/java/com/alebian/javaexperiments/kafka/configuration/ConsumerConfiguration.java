package com.alebian.javaexperiments.kafka.configuration;

import com.alebian.javaexperiments.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class ConsumerConfiguration {
    public static final Map<Class<? extends Throwable>, Boolean> EXCEPTION_SHOULD_RETRY = Map.of(
            DeserializationException.class, false,
            MessageConversionException.class, false,
            ConversionException.class, false,
            MethodArgumentResolutionException.class, false,
            NoSuchMethodException.class, false,
            ClassCastException.class, false,
            NullPointerException.class, false
    );

    private final KafkaProperties kafkaProperties;
    private final CustomDlqRecoverer dlqRecoverer;

    @Bean
    public ExponentialBackOff exponentialBackOff() {
        var backOff = new ExponentialBackOff();
        backOff.setInitialInterval(100);
        backOff.setMaxInterval(2000);
        backOff.setMultiplier(2);
        backOff.setMaxElapsedTime(1000);
        return backOff;
    }

    /**
     * This factory has the default {@link ConsumerRecordRecoverer}.
     */
    @Bean(name = Constants.Kafka.DEFAULT_DLQ_LISTENER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerDefaultDlqContainerFactory(KafkaTemplate<String, String> template, ExponentialBackOff backOff) {
        var factory = buildFactory();

        var errorHandler = new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(template), backOff);
        // Call the recoverer directly for certain exceptions
        errorHandler.setClassifications(EXCEPTION_SHOULD_RETRY, true);
        factory.setErrorHandler(errorHandler);

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));

        return factory;
    }

    /**
     * This factory has a custom {@link ConsumerRecordRecoverer}.
     */
    @Bean(name = Constants.Kafka.CUSTOM_DLQ_LISTENER_FACTORY)
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerCustomDlqContainerFactory(ExponentialBackOff backOff) {
        var factory = buildFactory();

        var errorHandler = new SeekToCurrentErrorHandler(dlqRecoverer, backOff);
        // Call the recoverer directly for certain exceptions
        errorHandler.setClassifications(EXCEPTION_SHOULD_RETRY, true);
        factory.setErrorHandler(errorHandler);
        factory.setStatefulRetry(true);

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));

        return factory;
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> buildFactory() {
        return new ConcurrentKafkaListenerContainerFactory<>();
    }
}
