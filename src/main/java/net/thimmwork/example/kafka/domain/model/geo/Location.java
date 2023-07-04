package net.thimmwork.example.kafka.domain.model.geo;

import java.math.BigDecimal;

public class Location {
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Location(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }
}
