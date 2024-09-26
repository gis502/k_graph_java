package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.CasualtyStats;


/**
 * 统计数据服务接口
 */
public interface CasualtyStatsService {

    /**
     * 根据地震 ID 获取统计数据
     * 获取统计数据（受伤人数、失联人数、遇难人数）
     *
     * @param eqid 地震 ID
     * @return CasualtyStats 对象
     */
    CasualtyStats getCasualtiesStatsById(String eqid);
}
