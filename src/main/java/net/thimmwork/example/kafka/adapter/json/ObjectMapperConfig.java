package net.thimmwork.example.kafka.adapter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
                .build();
    }
}
