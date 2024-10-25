package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.RoadDamage;
import com.ruoyi.system.service.RoadDamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class RoadDamageController {
    @Autowired
    private RoadDamageService roadDamageService;

    @GetMapping("/repairs")
    public List<RoadDamage> repairs(@RequestParam String eqid) {
        return roadDamageService.getRoadRepairsByEqid(eqid);
    }
}