package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.CasualtyStats;
import com.ruoyi.system.service.CasualtyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/casualty")
public class CasualtyStatsController {
    @Autowired
    private CasualtyStatsService casualtyStatsService;

    /**
     * 根据地震 ID 获取统计数据
     * 获取统计数据（newly_deceased, newly_missing, newly_injured）
     */
    @GetMapping("/sumCasById")
    public CasualtyStats getCasualtiesStatsById(@RequestParam String eqid) {
        return casualtyStatsService.getCasualtiesStatsById(eqid);
    }
}
