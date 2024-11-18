package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.RoadDamage;
import com.ruoyi.system.service.RoadDamageService;
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
public class RoadDamageController {
    @Autowired
    private RoadDamageService roadDamageService;

    @GetMapping("/repair")
    public List<RoadDamage> repairs(@RequestParam String eqid) {
        return roadDamageService.getRoadRepairsByEqid(eqid);
    }

    @GetMapping("/fromrepair")
    public AjaxResult fromrepair(@RequestParam("eqid")String eqid,
                                 @RequestParam("time")@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")LocalDateTime time){
        List<RoadDamage> roadDamageList = roadDamageService.fromrepair(eqid,time);
        return AjaxResult.success(roadDamageList);
    }
}