package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import com.ruoyi.system.service.PowerSupplyInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/power")
public class PowerSupplyInformationController {

    @Autowired
    private PowerSupplyInformationService powerSupplyInformationService;


    @GetMapping("/supply")
    public List<PowerSupplyInformation> getPowerSupply(@RequestParam String eqid){
        return powerSupplyInformationService.getPowerSupply(eqid);
    }
}
