package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.RedCrossDonations;
import com.ruoyi.system.service.CharityOrganizationDonationsService;
import com.ruoyi.system.service.RedCrossDonationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class CharityOrganizationDonationsController {

    @Autowired
    private CharityOrganizationDonationsService charityOrganizationDonationsService;



    @GetMapping("/charityorganizationdonations")
    public List<CharityOrganizationDonations> redcrossdonations(@RequestParam String eqid) {
        return charityOrganizationDonationsService.CharityOrganizationDonationsByEqId(eqid);
    }
}
