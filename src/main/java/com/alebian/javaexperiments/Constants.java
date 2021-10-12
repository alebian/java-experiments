package com.alebian.javaexperiments;

public final class Constants {
    private static final String CONSTANTS_CLASS = "This class is a constant class";

    private Constants() {
        throw new IllegalStateException(CONSTANTS_CLASS);
    }

    public static final class Kafka {
        public static final String CONSUMER_GROUP = "experiments-group";

        public static final String DEFAULT_LISTENER_FACTORY = "kafka-listener-default";
        public static final String DEFAULT_DLQ_LISTENER_FACTORY = "kafka-listener-default-dlq";
        public static final String CUSTOM_DLQ_LISTENER_FACTORY = "kafka-listener-custom-dlq";

        private Kafka() {
            throw new IllegalStateException(CONSTANTS_CLASS);
        }
    }
}
