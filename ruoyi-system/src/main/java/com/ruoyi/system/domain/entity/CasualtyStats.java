package com.ruoyi.system.domain.entity;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CasualtyStats {
    /**
     * 受伤总人数
     */
    private Integer injuryCount;

    /**
     * 失联总人数
     */
    private Integer missingCount;

    /**
     * 遇难总人数
     */
    private Integer deathCount;

    public CasualtyStats(Integer injuryCount, Integer missingCount, Integer deathCount) {
        this.injuryCount = injuryCount;
        this.missingCount = missingCount;
        this.deathCount = deathCount;
    }


}
