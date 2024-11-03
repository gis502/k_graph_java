package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.RedCrossDonations;
import com.ruoyi.system.service.GovernmentDepartmentDonationsService;
import com.ruoyi.system.service.RedCrossDonationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class RedCrossDonationsController {
    @Autowired
    private RedCrossDonationsService redCrossDonationsService;



    @GetMapping("/redcrossdonations")
    public List<RedCrossDonations> redcrossdonations(@RequestParam String eqid) {
        return redCrossDonationsService.RedCrossDonationsByEqId(eqid);
    }

}
