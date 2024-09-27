package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.entity.CasualtyStats;
//import com.ruoyi.system.domain.export.CasualtyStats;
//import com.ruoyi.system.domain.entity.YaanCasualties;
import com.ruoyi.system.mapper.CasualtyStatsMapper;
import com.ruoyi.system.service.CasualtyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * CasualtyStatsService 实现类
 */
@Service
public class CasualtyStatsServiceImpl implements CasualtyStatsService {
    @Autowired
    private CasualtyStatsMapper casualtyStatsMapper;

    @Override
    public CasualtyStats getCasualtiesStatsById(String eqid) {
        // 查询统计数据
        List<CasualtyStats> listStats = casualtyStatsMapper.getCasualtiesStatsById(eqid);
        CasualtyStats stats = new CasualtyStats();
        stats.setInjuryCount(0);
        stats.setMissingCount(0);
        stats.setDeathCount(0);
        //判断listStats集合内的第一个元素是否为空
        if (listStats.get(0) != null) {
            stats = listStats.get(0);
        }
        // 封装到 CasualtyStats 对象中
        return new CasualtyStats(stats.getInjuryCount(), stats.getMissingCount(), stats.getDeathCount());
    }

}
