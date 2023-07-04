package net.thimmwork.example.kafka;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.prometheus.client.Counter;
import net.thimmwork.example.initializer.DatabaseInitializer;
import net.thimmwork.example.initializer.KafkaInitializer;
import net.thimmwork.example.kafka.adapter.kafka.DwdNowcastConsumer;
import net.thimmwork.example.kafka.adapter.testcontainers.kafka.KafkaProducerProperties;
import net.thimmwork.example.kafka.domain.model.geo.Location;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = { DatabaseInitializer.class, KafkaInitializer.class })
class TimeSensitiveJsonKafkaExampleIT {

	KafkaProducer<String, String> producer;

	@Autowired
	DwdNowcastConsumer exampleKafkaConsumer;

	@MockBean(answer = Answers.RETURNS_DEEP_STUBS)
	Counter consumedMessagesCounter;

	@Autowired
	JsonMapper jsonMapper;

	@Value("${spring.kafka.consumer.example-topic.topic}")
	String topicName;

	@MockBean
	Clock clock;

	@MockBean
	Location location;

	@BeforeEach
	public void setUp() {
		producer = new KafkaProducer<>(KafkaProducerProperties.producerProperties());
	}

	@AfterEach
	public void tearDown() {
		producer.close();
	}

	@Test
	void time_sensitive_kafka_message_filter_test() throws IOException, ExecutionException, InterruptedException, TimeoutException {
		Instant mondayJuly3rd2023_1640 = Instant.parse("2023-07-03T16:40:00Z");
		when(clock.instant()).thenReturn(mondayJuly3rd2023_1640);
		when(location.getLatitude()).thenReturn(new BigDecimal("53.70077"));
		when(location.getLongitude()).thenReturn(new BigDecimal("9.16975"));

		var stream = this.getClass().getClassLoader().getResourceAsStream("json/dvd-nowcast-real-example.json");
		var warningJson = IOUtils.toString(new BufferedInputStream(stream).readAllBytes(), "UTF-8");
		producer.send(new ProducerRecord<>(topicName, UUID.randomUUID().toString(), warningJson)).get();

		//wait for asynchronous consumer to increase metric to make sure the message has been processed
		verify(consumedMessagesCounter.labels(topicName, "success"), timeout(5000).times(1))
				.inc();
	}
}
