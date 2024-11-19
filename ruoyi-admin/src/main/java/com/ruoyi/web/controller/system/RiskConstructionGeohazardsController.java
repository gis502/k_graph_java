package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import com.ruoyi.system.service.RiskConstructionGeohazardsService;
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
public class RiskConstructionGeohazardsController {


    @Autowired
    private RiskConstructionGeohazardsService riskConstructionGeohazardsService;



    @GetMapping("/riskconstructiongeohazards")
    public List<RiskConstructionGeohazards> riskconstructiongeohazards(@RequestParam String eqid) {
        return riskConstructionGeohazardsService.RiskConstructionGeohazardsByEqId(eqid);
    }

    @GetMapping("/fromRiskConstructionGeohazards")
    public AjaxResult fromRiskConstructionGeohazards(@RequestParam("eqid") String eqid,
                                                     @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time)  {
        List<RiskConstructionGeohazards> riskConstructionGeohazardsList = riskConstructionGeohazardsService.fromRiskConstructionGeohazards(eqid, time);
        return AjaxResult.success(riskConstructionGeohazardsList);
    }
}
