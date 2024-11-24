package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.MaterialDonation;
import com.ruoyi.system.service.CharityOrganizationDonationsService;
import com.ruoyi.system.service.MaterialDonationService;
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
public class MaterialDonationController {

    @Autowired
    private MaterialDonationService materialDonationService;

    @GetMapping("/materialdonation")
    public List<MaterialDonation> materialdonation(@RequestParam String eqid) {
        return materialDonationService.MaterialDonationByEqId(eqid);
    }

    @GetMapping("/fromMaterialDonation")
    public AjaxResult fromMaterialDonation(@RequestParam("eqid") String eqid,
                                           @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<MaterialDonation> materialDonationList = materialDonationService.fromMaterialDonation(eqid, time);
        return AjaxResult.success(materialDonationList);
    }

}
