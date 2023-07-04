package net.thimmwork.example.kafka.adapter.json.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.thimmwork.example.kafka.domain.model.geo.MapPolygon;

import java.math.BigDecimal;
import java.util.List;

public record Region(List<BigDecimal> polygon, List<Integer> triangles) {

    @JsonIgnore
    public MapPolygon getMapPolygon() {
        return new MapPolygon(polygon);
    }
}
