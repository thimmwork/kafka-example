package net.thimmwork.example.kafka.adapter.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Counter;
import net.thimmwork.example.kafka.adapter.json.in.NowcastWarningsUpdatedMessage;
import net.thimmwork.example.kafka.adapter.metrics.Metrics;
import net.thimmwork.example.kafka.domain.model.geo.Location;
import net.thimmwork.example.kafka.domain.service.NowcastProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class DwdNowcastConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DwdNowcastConsumer.class);
    private static final TypeReference<NowcastWarningsUpdatedMessage> MESSAGE_TYPE = new TypeReference<>() {};

    private final Clock clock;
    private final ObjectMapper objectMapper;
    private final Location location;
    private final NowcastProcessor processor;
    private final RetryTemplate retryTemplate;
    private final Counter consumedMessagesCounter;
    private final long maxAgeInMinutes;


    @Autowired
    public DwdNowcastConsumer(Clock clock,
                              ObjectMapper jsonMapper,
                              @Qualifier(Metrics.CONSUMED_KAFKA_MESSAGES_COUNTER) Counter consumedMessagesCounter,
                              @Value("${net.thimmwork.nowcast.max-meter-age-in-minutes:1440}") int maxAgeInMinutes,
                              Location location,
                              NowcastProcessor nowcastProcessor) {
        this.clock = clock;
        this.objectMapper = jsonMapper;
        this.location = location;
        this.processor = nowcastProcessor;
        this.consumedMessagesCounter = consumedMessagesCounter;
        this.maxAgeInMinutes = maxAgeInMinutes;

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
            NowcastWarningsUpdatedMessage message = objectMapper.readValue(consumerRecord.value(), MESSAGE_TYPE);

            if (!isRelevant(message)) {
                return;
            }

            retryTemplate.execute(context -> processor.onWarningUpdated(message));

            consumedMessagesCounter.labels(topic, "success").inc();
            LOGGER.info("Successfully processed kafka message with key {}", consumerRecord.key());
            ack.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error processing kafka message with key {}", consumerRecord.key(), e);
            consumedMessagesCounter.labels(topic, "failure").inc();
        }
    }

    private boolean isRelevant(NowcastWarningsUpdatedMessage message) {
        var deprecationThresholdTime = ChronoUnit.MINUTES.addTo(clock.instant(), -maxAgeInMinutes);
        if (Instant.ofEpochMilli(message.getTime()).isBefore(deprecationThresholdTime)) {
            LOGGER.debug("Message is irrelevant because it is deprecated");
            return false;
        }
        var isRelevantForRegion = message.getWarnings().stream()
                .flatMap(warning -> warning.getRegions().stream())
                .anyMatch(region -> region.getMapPolygon().contains(location));
        if (!isRelevantForRegion) {
            LOGGER.debug("Message is irrelevant because none of the regions contains our location");
            return false;
        }
        return true;
    }
}
