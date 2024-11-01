package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import com.ruoyi.system.service.BarrierLakeSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
