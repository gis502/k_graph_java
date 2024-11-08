package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.MaterialDonation;
import com.ruoyi.system.service.CharityOrganizationDonationsService;
import com.ruoyi.system.service.MaterialDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class MaterialDonationController {

    @Autowired
    private MaterialDonationService materialDonationService;



    @GetMapping("/materialdonation")
    public List<MaterialDonation> materialdonation(@RequestParam String eqid) {
        return materialDonationService.MaterialDonationByEqId(eqid);
    }
}
