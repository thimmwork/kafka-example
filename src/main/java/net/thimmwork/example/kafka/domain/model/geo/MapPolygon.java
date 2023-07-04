package net.thimmwork.example.kafka.domain.model.geo;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MapPolygon {
    private static final BigDecimal ONE_MILLION = BigDecimal.valueOf(1_000_000);
    private Polygon polygon;

    public MapPolygon(List<BigDecimal> points) {
        this.polygon = parseToPolygon(points);
    }

    public static Polygon parseToPolygon(List<BigDecimal> points) {
        var numberOfCoords = points.size() / 2;
        if (numberOfCoords * 2 != points.size()) {
            throw new IllegalArgumentException("Odd number of points: " + points.size() + "! Expeced even number representing latitudes and longitudes.");
        }
        int[] latitudes = new int[numberOfCoords];
        int[] longitudes = new int[numberOfCoords];
        for (int i=0; i<numberOfCoords; i++) {
            latitudes[i] = points.get(2*i).multiply(ONE_MILLION).intValue();
            longitudes[i] = points.get(2*i+1).multiply(ONE_MILLION).intValue();
        }
        return new Polygon(longitudes, latitudes, numberOfCoords);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public boolean contains(Location coordinate) {
        var y = coordinate.getLatitude().multiply(ONE_MILLION).intValue();
        var x = coordinate.getLongitude().multiply(ONE_MILLION).intValue();
        return polygon.contains(x, y);
    }
}
