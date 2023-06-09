package net.thimmwork.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.prometheus.client.Counter;
import net.thimmwork.example.initializer.DatabaseInitializer;
import net.thimmwork.example.initializer.KafkaInitializer;
import net.thimmwork.example.kafka.adapter.json.in.NowcastWarning;
import net.thimmwork.example.kafka.adapter.json.in.NowcastWarningsUpdatedMessage;
import net.thimmwork.example.kafka.adapter.json.in.Region;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = { DatabaseInitializer.class, KafkaInitializer.class })
class KafkaExampleApplicationTests {

	@Value("${spring.kafka.consumer.example-topic.topic}")
	String topicName;

	KafkaProducer<String, String> producer;

	@Autowired
	DwdNowcastConsumer exampleKafkaConsumer;

	@MockBean(answer = Answers.RETURNS_DEEP_STUBS)
	Counter consumedMessagesCounter;

	@Autowired
	JsonMapper jsonMapper;

	@BeforeEach
	public void setUp() {
		producer = new KafkaProducer<>(KafkaProducerProperties.producerProperties());
	}

	@AfterEach
	public void tearDown() {
		producer.close();
	}

	@Test
	void kafka_smoke_test() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
		UUID warningId = UUID.randomUUID();

		List<BigDecimal> polygonCoords = List.of(
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLatitude().subtract(BigDecimal.ONE),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLongitude(),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLatitude(),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLongitude().subtract(BigDecimal.ONE),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLatitude().add(BigDecimal.ONE),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLongitude(),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLatitude(),
				new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825")).getLongitude().add(BigDecimal.ONE)
		);

		NowcastWarningsUpdatedMessage warningsUpdatedMessage = new NowcastWarningsUpdatedMessage(System.currentTimeMillis(), List.of(
				new NowcastWarning(warningId.toString(), Instant.now().minusSeconds(10).toEpochMilli(), Instant.now().plusSeconds(24 * 3600L).toEpochMilli(),
						List.of(
								new Region(polygonCoords, List.of())
						))),
				null);
		producer.send(new ProducerRecord<>(topicName, warningId.toString(), jsonMapper.writeValueAsString(warningsUpdatedMessage)))
				.get(1_000, TimeUnit.MILLISECONDS);

		//wait for asynchronous consumer to increase metric to make sure the message has been processed
		verify(consumedMessagesCounter.labels(topicName, "success"), timeout(5000).times(1))
				.inc();
	}
}
