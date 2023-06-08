package net.thimmwork.example.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        KAFKA.start();
        var bootstrapServers = KAFKA.getBootstrapServers();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                "spring.kafka.bootstrap-servers=" + bootstrapServers);
    }
}
