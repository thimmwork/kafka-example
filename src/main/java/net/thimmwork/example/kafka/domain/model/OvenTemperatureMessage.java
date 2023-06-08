package net.thimmwork.example.kafka.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class OvenTemperatureMessage {
    private String uuid;
    private int temperatureInCelsius;
    private Instant timestamp;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OvenTemperatureMessage(@JsonProperty("uuid") String uuid,
                                  @JsonProperty("temperatureInCelsius") int temperatureInCelsius,
                                  @JsonProperty("timestamp") Instant timestamp) {
        this.uuid = uuid;
        this.temperatureInCelsius = temperatureInCelsius;
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public int getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
