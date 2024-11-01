package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import com.ruoyi.system.service.RiskConstructionGeohazardsService;
import com.ruoyi.system.service.SecondaryDisasterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class RiskConstructionGeohazardsController {


    @Autowired
    private RiskConstructionGeohazardsService riskConstructionGeohazardsService;



    @GetMapping("/riskconstructiongeohazards")
    public List<RiskConstructionGeohazards> riskconstructiongeohazards(@RequestParam String eqid) {
        return riskConstructionGeohazardsService.RiskConstructionGeohazardsByEqId(eqid);
    }
}
