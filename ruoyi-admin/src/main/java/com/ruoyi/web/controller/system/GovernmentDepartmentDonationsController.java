package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import com.ruoyi.system.service.GovernmentDepartmentDonationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class GovernmentDepartmentDonationsController {
    @Autowired
    private GovernmentDepartmentDonationsService governmentDepartmentDonationsService;



    @GetMapping("/governmentdepartmentdonations")
    public List<GovernmentDepartmentDonations> governmentdepartmentdonations(@RequestParam String eqid) {
        return governmentDepartmentDonationsService.GovernmentDepartmentDonationsByEqId(eqid);
    }
}
