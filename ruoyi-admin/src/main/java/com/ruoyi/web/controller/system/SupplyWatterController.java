package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.SupplyWater;
import com.ruoyi.system.service.SupplyWaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SupplyWatterController {
    @Autowired
    private SupplyWaterService supplyWaterService;

    @GetMapping("/supplyWatterList")
    public List<SupplyWater> supplyWaterList(@RequestParam(required = false) String eqid) {
        return supplyWaterService.getSupplyWaterById(eqid);
    }
}
