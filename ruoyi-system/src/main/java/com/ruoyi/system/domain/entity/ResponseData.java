package com.ruoyi.system.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private List<double[]> pathSegments;
    private int totalDistance;
    private List<String> pathInstructions;
    private double carTime;
    private double humanTime;
}
