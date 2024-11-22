package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import com.ruoyi.system.service.GovernmentDepartmentDonationsService;
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
public class GovernmentDepartmentDonationsController {
    @Autowired
    private GovernmentDepartmentDonationsService governmentDepartmentDonationsService;



    @GetMapping("/governmentdepartmentdonations")
    public List<GovernmentDepartmentDonations> governmentdepartmentdonations(@RequestParam String eqid) {
        return governmentDepartmentDonationsService.GovernmentDepartmentDonationsByEqId(eqid);
    }

    @GetMapping("/fromGovernmentDepartmentDonations")
    public AjaxResult fromGovernmentDepartmentDonations(@RequestParam("eqid") String eqid,
                                                        @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<GovernmentDepartmentDonations> governmentDepartmentDonationsList = governmentDepartmentDonationsService.fromGovernmentDepartmentDonations(eqid, time);
        return AjaxResult.success(governmentDepartmentDonationsList);
    }

}
