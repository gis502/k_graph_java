package com.ruoyi.system.domain.entity;

import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ZhongduanVillage {
    private Integer RoadBlockVillage; // 道路
    private Integer CurrentInterruptedVillages; // 通信
    private Integer CurrentlyBlackedOutVillages; // 供电
    private LocalDateTime insertTime;
}
