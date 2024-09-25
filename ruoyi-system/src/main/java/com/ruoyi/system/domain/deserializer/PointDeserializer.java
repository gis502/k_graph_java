package com.ruoyi.system.domain.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode coordinatesNode = node.get("coordinates");

        if (coordinatesNode == null || !coordinatesNode.isArray() || coordinatesNode.size() != 2) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        double x = coordinatesNode.get(0).asDouble();
        double y = coordinatesNode.get(1).asDouble();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

}
