package com.ruoyi.web.controller.common;

import lombok.Data;

@Data
public class DrivingRouteRequest {
    private String[] origin;
    private String[] destination;
    private String avoidpolygons;

}
