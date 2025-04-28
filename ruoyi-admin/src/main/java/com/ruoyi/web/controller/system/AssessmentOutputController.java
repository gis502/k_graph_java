package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.service.IAssessmentOutputService;
import com.ruoyi.web.api.service.SeismicTableTriggerService;
import com.ruoyi.web.api.service.SismiceMergencyAssistanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2025-04-19 23:53
 * @description: 产出结果控制类
 */

@Slf4j
@RestController
public class AssessmentOutputController {

    @Resource
    private IAssessmentOutputService assessmentOutputService;
    @Resource
    private SismiceMergencyAssistanceService sismiceMergencyAssistanceService;

    @Resource
    private SeismicTableTriggerService seismicTableTriggerService;


    @GetMapping("/system/getMap")
    public String getMap(@RequestParam(value = "eqId") String eqId, @RequestParam(value = "eqqueueId") String eqqueueId) {
        log.info("eqid：{}", eqId);
        try {
            String outputs = assessmentOutputService.gainMap(eqId,eqqueueId);
            return outputs;
        } catch (Exception e) {
            e.printStackTrace();
            return "获取地图数据失败";
        }
    }

    @PostMapping("/system/getReport")
    public String getReport(@RequestBody EqEventTriggerDTO params){
        System.out.println("params"+params);
        // 调用 tableFile 方法--异步获取辅助决策报告(一)
        seismicTableTriggerService.tableFile(params);
        return "ok";
    }



}
