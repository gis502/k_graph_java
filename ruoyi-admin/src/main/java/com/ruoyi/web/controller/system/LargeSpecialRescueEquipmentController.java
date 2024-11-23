package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.LargeSpecialRescueEquipment;
import com.ruoyi.system.service.LargeSpecialRescueEquipmentService;
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
public class LargeSpecialRescueEquipmentController {
    @Autowired
    private LargeSpecialRescueEquipmentService largeSpecialRescueEquipmentService;



    @GetMapping("/largespecialrescueequipment")
    public List<LargeSpecialRescueEquipment> largespecialrescueequipment(@RequestParam String eqid) {
        return largeSpecialRescueEquipmentService.LargeSpecialRescueEquipmentByEqId(eqid);
    }

    @GetMapping("/fromLargeSpecialRescueEquipment")
    public AjaxResult fromLargeSpecialRescueEquipment(@RequestParam("eqid") String eqid,
                                                      @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<LargeSpecialRescueEquipment> largeSpecialRescueEquipmentList = largeSpecialRescueEquipmentService.fromLargeSpecialRescueEquipment(eqid, time);
        return AjaxResult.success(largeSpecialRescueEquipmentList);
    }

}
