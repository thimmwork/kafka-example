package net.thimmwork.example.kafka.adapter.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Bean
    public ConsumerFactory<String, String> singleConsumerFactory(KafkaProperties kafkaProperties) {
        var kafkaConfiguration = buildKafkaConfigurationWithDefaultValue(kafkaProperties);
        return new DefaultKafkaConsumerFactory<>(kafkaConfiguration);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> defaultListenerContainerFactory(
            ConsumerFactory<String, String> singleConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setConsumerFactory(singleConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    private Map<String, Object> buildKafkaConfigurationWithDefaultValue(KafkaProperties kafkaProperties) {
        var kafkaConfiguration = kafkaProperties.buildConsumerProperties();
        kafkaConfiguration.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        kafkaConfiguration.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return kafkaConfiguration;
    }

}
