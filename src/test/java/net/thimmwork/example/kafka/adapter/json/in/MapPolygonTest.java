package net.thimmwork.example.kafka.adapter.json.in;

import net.thimmwork.example.kafka.domain.model.geo.Location;
import net.thimmwork.example.kafka.domain.model.geo.MapPolygon;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MapPolygonTest {
    @Test
    void parse_to_polygon() {
        List<BigDecimal> coords = List.of(
                new BigDecimal("50.943539"),
                new BigDecimal("6.957673"),
                new BigDecimal("50.940727"),
                new BigDecimal("6.955066"),
                new BigDecimal("50.940727"),
                new BigDecimal("6.955066"),
                new BigDecimal("50.941011"),
                new BigDecimal("6.961632")
        );

        var polygon = MapPolygon.parseToPolygon(coords);

        assertTrue(polygon.contains(6958250, 50941280));

        var mapPolygon = new MapPolygon(coords);

        assertTrue(mapPolygon.contains(new Location(new BigDecimal("50.94128"), new BigDecimal("6.95825"))));
    }
}