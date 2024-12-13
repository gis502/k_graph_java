package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.system.domain.deserializer.PointDeserializer;
import org.locationtech.jts.geom.Point;

public class GeometryDTO {

    @JsonDeserialize(using = PointDeserializer.class)
    private Point point;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
