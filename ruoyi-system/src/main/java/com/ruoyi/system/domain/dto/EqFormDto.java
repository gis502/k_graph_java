package com.ruoyi.system.domain.dto;

import lombok.Data;

@Data
public class EqFormDto {
    private String earthquakeName;
    private String startTime;  // 前端传递的开始时间
    private String endTime;    // 前端传递的结束时间
    private String startMagnitude;
    private String endMagnitude;
    private String startDepth;
    private String endDepth;
}
