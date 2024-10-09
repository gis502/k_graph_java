package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.service.CasualtyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/casualty")
public class CasualtyReportController {

    @Autowired
    private CasualtyReportService casualtyReportService;

    /**
     * 根据地震 ID 获取统计数据
     * 获取统计数据（newly_deceased, newly_missing, newly_injured）
     */
    @GetMapping("/sumCasById")
    public CasualtyReport getCasualtiesStatsById(@RequestParam String eqid) {
        return casualtyReportService.getCasualtiesStatsById(eqid);
    }

    @GetMapping("/gettotal")
    public List<CasualtyReport> getTotal(@RequestParam String eqid) {
        return casualtyReportService.getTotal(eqid);
    }
}
