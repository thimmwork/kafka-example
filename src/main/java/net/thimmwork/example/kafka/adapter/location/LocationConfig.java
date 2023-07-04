package net.thimmwork.example.kafka.adapter.location;

import net.thimmwork.example.kafka.domain.model.geo.Location;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LocationConfig {
    @Bean
    public Location location() {
        return new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825"));
    }
}
