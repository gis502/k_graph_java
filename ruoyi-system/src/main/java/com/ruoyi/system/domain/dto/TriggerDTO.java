package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: xiaodemos
 * @date: 2025-04-19 14:49
 * @description: 触发dto
 */

@Data
@NoArgsConstructor
public class TriggerDTO implements Serializable {

    private String eqName;  // 地震名称
    private String eqAddr;  //震发位置
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eqTime;   // 地震时间
    private double longitude;   // 经度
    private double latitude;    // 纬度
    private double eqDepth;   // 震源深度
    private double magnitude;   // 震级
    private String eqType;  // 地震类型
    private String fullName;

}
