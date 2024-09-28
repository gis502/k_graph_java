package com.ruoyi.web.controller.common;

import lombok.Data;

@Data
public class EqQueryDTO {
    private String position;
    private String time;
    private String startMagnitude;
    private String endMagnitude;
    private String startDepth;
    private String endDepth;

    // Getters and Setters
}
