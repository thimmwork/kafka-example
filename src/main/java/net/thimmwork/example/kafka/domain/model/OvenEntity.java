package net.thimmwork.example.kafka.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class OvenEntity {
    @Id
    private UUID id;

    @Column(name = "temperature_c")
    private int temperatureInC;

    public int getTemperatureInC() {
        return temperatureInC;
    }

    public void setTemperatureInC(int temperatureInC) {
        this.temperatureInC = temperatureInC;
    }

    public UUID getId() {
        return id;
    }
}
