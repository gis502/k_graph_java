package com.ruoyi.system.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author: xiaodemos
 * @date: 2025-04-19 14:49
 * @description: 触发dto
 */

@Data
@NoArgsConstructor
public class TriggerDTO {

    private String eqName;  // 地震名称
    private String eqAddr;  //震发位置
    private LocalDateTime eqTime;   // 地震时间
    private double longitude;   // 经度
    private double latitude;    // 纬度
    private double eqDepth;   // 震源深度
    private double magnitude;   // 震级
    private String eqType;  // 地震类型

}
