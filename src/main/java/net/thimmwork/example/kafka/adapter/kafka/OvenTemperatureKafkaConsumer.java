package net.thimmwork.example.kafka.adapter.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Counter;
import net.thimmwork.example.kafka.adapter.metrics.Metrics;
import net.thimmwork.example.kafka.domain.model.OvenTemperatureMessage;
import net.thimmwork.example.kafka.domain.service.OvenTemperatureChangedProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.stereotype.Service;

@Service
public class OvenTemperatureKafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OvenTemperatureKafkaConsumer.class);
    private static final TypeReference<OvenTemperatureMessage> MESSAGE_TYPE = new TypeReference<>() {};

    private final OvenTemperatureChangedProcessor processor;
    private final ObjectMapper objectMapper;
    private final OvenTemperatureChangedProcessor ovenTemperatureChangedProcessor;
    private final RetryTemplate retryTemplate;
    private final Counter consumedMessagesCounter;


    @Autowired
    public OvenTemperatureKafkaConsumer(OvenTemperatureChangedProcessor processor,
                                        ObjectMapper jsonMapper,
                                        @Qualifier(Metrics.CONSUMED_KAFKA_MESSAGES_COUNTER) Counter consumedMessagesCounter,
                                        OvenTemperatureChangedProcessor ovenTemperatureChangedProcessor) {
        this.processor = processor;
        this.objectMapper = jsonMapper;
        this.ovenTemperatureChangedProcessor = ovenTemperatureChangedProcessor;
        this.consumedMessagesCounter = consumedMessagesCounter;

        this.retryTemplate = new RetryTemplateBuilder()
                .retryOn(TransientDataAccessException.class)
                .traversingCauses()
                .maxAttempts(3)
                .build();
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.example-topic.topic}",
            groupId = "${spring.kafka.consumer.example-topic.group-id}"
    )
    public void consumeExampleTopic(
            ConsumerRecord<String, String> consumerRecord,
            Acknowledgment ack
    ) {
        var topic = consumerRecord.topic();
        try (MDC.MDCCloseable c1 = MDC.putCloseable("kafka.topic", topic);
             MDC.MDCCloseable c2 = MDC.putCloseable("kafka.offset", String.valueOf(consumerRecord.offset()));
             MDC.MDCCloseable c3 = MDC.putCloseable("kafka.partition", String.valueOf(consumerRecord.partition()));
             MDC.MDCCloseable c4 = MDC.putCloseable("kafka.key", consumerRecord.key())
        ) {
            OvenTemperatureMessage message = objectMapper.readValue(consumerRecord.value(), MESSAGE_TYPE);

            retryTemplate.execute(context -> ovenTemperatureChangedProcessor.onTemperatureChange(message));

            consumedMessagesCounter.labels(topic, "success").inc();
            LOGGER.info("Successfully processed kafka message with key {}", consumerRecord.key());
            ack.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error processing kafka message with key {}", consumerRecord.key(), e);
            consumedMessagesCounter.labels(topic, "failure").inc();
        }
    }
}
