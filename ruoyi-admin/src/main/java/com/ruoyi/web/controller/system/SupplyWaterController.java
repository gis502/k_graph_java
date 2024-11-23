package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.SupplyWater;
import com.ruoyi.system.service.SupplyWaterService;
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
public class SupplyWaterController {
    @Autowired
    private SupplyWaterService supplyWaterService;

    @GetMapping("/supplyWaterList")
    public List<SupplyWater> supplyWaterList(@RequestParam(required = false) String eqid) {
        return supplyWaterService.getSupplyWaterById(eqid);
    }

    @GetMapping("/fromSupplyWater")
    public AjaxResult fromSupplyWater(@RequestParam("eqid") String eqid,
                                      @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<SupplyWater> supplyWaterList = supplyWaterService.fromSupplyWater(eqid, time);
        return AjaxResult.success(supplyWaterList);
    }

}
