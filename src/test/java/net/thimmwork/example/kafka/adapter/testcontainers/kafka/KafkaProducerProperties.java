package net.thimmwork.example.kafka.adapter.testcontainers.kafka;

import net.thimmwork.example.initializer.KafkaInitializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class KafkaProducerProperties {
    public static Properties producerProperties() {
        var properties = new Properties();
        var stringSerializerClass = StringSerializer.class.getCanonicalName();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInitializer.KAFKA.getBootstrapServers());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, stringSerializerClass);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, stringSerializerClass);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        return properties;
    }

}
