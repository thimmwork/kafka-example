package net.thimmwork.example.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.prometheus.client.Counter;
import net.thimmwork.example.initializer.DatabaseInitializer;
import net.thimmwork.example.initializer.KafkaInitializer;
import net.thimmwork.example.kafka.adapter.kafka.OvenTemperatureKafkaConsumer;
import net.thimmwork.example.kafka.domain.model.OvenTemperatureMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
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

import java.time.Instant;
import java.util.Properties;
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
	String topicOvenTemperature;

	KafkaProducer<String, String> producer;

	@Autowired
	OvenTemperatureKafkaConsumer exampleKafkaConsumer;

	@MockBean(answer = Answers.RETURNS_DEEP_STUBS)
	Counter consumedMessagesCounter;

	@Autowired
	JsonMapper jsonMapper;

	@BeforeEach
	public void setUp() {
		producer = new KafkaProducer<>(producerProperties());
	}

	@AfterEach
	public void tearDown() {
		producer.close();
	}

	private static Properties producerProperties() {
		var properties = new Properties();
		var stringSerializerClass = StringSerializer.class.getCanonicalName();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInitializer.KAFKA.getBootstrapServers());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, stringSerializerClass);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, stringSerializerClass);
		properties.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
		return properties;
	}

	@Test
	void kafka_smoke_test() throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
		UUID ovenId = UUID.randomUUID();

		OvenTemperatureMessage ovenTemperatureMessage = new OvenTemperatureMessage(ovenId.toString(), 90, Instant.now());
		producer.send(new ProducerRecord<>(topicOvenTemperature, ovenId.toString(), jsonMapper.writeValueAsString(ovenTemperatureMessage)))
				.get(1_000, TimeUnit.MILLISECONDS);

		//wait for asynchronous consumer to increase metric to make sure the message has been processed
		verify(consumedMessagesCounter.labels(topicOvenTemperature, "success"), timeout(5000).times(1))
				.inc();
	}
}
