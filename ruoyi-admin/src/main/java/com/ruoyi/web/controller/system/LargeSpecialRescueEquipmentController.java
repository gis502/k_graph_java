package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.LargeSpecialRescueEquipment;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import com.ruoyi.system.service.LargeSpecialRescueEquipmentService;
import com.ruoyi.system.service.SecondaryDisasterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
