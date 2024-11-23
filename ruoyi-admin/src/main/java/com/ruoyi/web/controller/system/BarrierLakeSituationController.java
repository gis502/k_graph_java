package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.service.BarrierLakeSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/system")
public class BarrierLakeSituationController {

    @Autowired
    private BarrierLakeSituationService barrierLakeSituationService;



    @GetMapping("/barrierlakesituation")
    public List<BarrierLakeSituation> barrierlakesituation(@RequestParam String eqid) {
        return barrierLakeSituationService.BarrierLakeSituationByEqId(eqid);
    }

    @GetMapping("/fromBarrierLakeSituation")
    public AjaxResult fromBarrierLakeSituation(@RequestParam("eqid") String eqid,
                                               @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<BarrierLakeSituation> barrierLakeSituationList = barrierLakeSituationService.fromBarrierLakeSituation(eqid, time);
        return AjaxResult.success(barrierLakeSituationList);
    }

}
