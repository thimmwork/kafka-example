package net.thimmwork.example.kafka.adapter.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Metrics {
    public static final String CONSUMED_KAFKA_MESSAGES_COUNTER = "counter_kafka_messages_consumed";

    @Bean
    @ConditionalOnMissingBean
    CollectorRegistry collectorRegistry() {
        return CollectorRegistry.defaultRegistry;
    }

    @Bean(name = CONSUMED_KAFKA_MESSAGES_COUNTER)
    public Counter consumedKafkaMessagesCounter(CollectorRegistry collectorRegistry) {
        return Counter.build()
                .name("kafka_mesages_consumed")
                .labelNames("topic", "result")
                .help("number of consumed kafka messages")
                .create()
                .register(collectorRegistry);
    }
}
