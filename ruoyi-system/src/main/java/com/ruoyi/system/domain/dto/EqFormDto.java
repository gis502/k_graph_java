package com.ruoyi.system.domain.dto;

import lombok.Data;

@Data
public class EqFormDto {
    private String earthquakeName;
    private String occurrenceTime;
    private String startMagnitude;
    private String endMagnitude;
    private String startDepth;
    private String endDepth;
}
