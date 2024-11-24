package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.CasualtyReport;
import com.ruoyi.system.service.CasualtyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/getCasualty")
    public List<CasualtyReport> getAll(@RequestParam String eqid) {
        return casualtyReportService.getCasualty(eqid);
    }
    //返回离提供时间最近的一条数据
    @GetMapping("/fromCasualty")
    public AjaxResult fromCasualty(@RequestParam("eqid") String eqid,
                                   @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time){
        List<Map<String, Object>> aftercasualtyList  = casualtyReportService.fromCasualty(eqid,time);
        return AjaxResult.success(aftercasualtyList);
    }
    //获取所有记录
    @GetMapping("/getAllRecordInfo")
    public List<CasualtyReport> getAllRecordInfo(@RequestParam String eqid) {
        return casualtyReportService.getAllRecordInfo(eqid);
    }
}
