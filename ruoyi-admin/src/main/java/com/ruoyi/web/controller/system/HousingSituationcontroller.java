package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.HousingSituation;
import com.ruoyi.system.domain.entity.SupplySituation;
import com.ruoyi.system.service.HousingSituationService;
import com.ruoyi.system.service.SupplySituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class HousingSituationcontroller {
    @Autowired
    private HousingSituationService housingSituationService;

    @GetMapping("/HousingSituationList")
    public List<HousingSituation> supplySituationList(@RequestParam(required = false) String eqid) {
        return housingSituationService.getHousingSituationById(eqid);
    }
}
