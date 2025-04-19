package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.AssessmentOutput;
import com.ruoyi.system.service.IAssessmentOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
