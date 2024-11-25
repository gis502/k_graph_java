package com.ruoyi.web.controller.system;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import com.ruoyi.system.service.DistrictEconomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class DistrictEconomyController {
    @Autowired
    private DistrictEconomyService districtEconomyService;

    @GetMapping("/getDistrictEconomy")
    public List<DistrictEconomy> getDistrictEconomy(@RequestParam Integer time) {
        return districtEconomyService.getDistrictEconomy(time);
    }
}
