package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.RedCrossDonations;
import com.ruoyi.system.service.GovernmentDepartmentDonationsService;
import com.ruoyi.system.service.RedCrossDonationsService;
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
public class RedCrossDonationsController {
    @Autowired
    private RedCrossDonationsService redCrossDonationsService;



    @GetMapping("/redcrossdonations")
    public List<RedCrossDonations> redcrossdonations(@RequestParam String eqid) {
        return redCrossDonationsService.RedCrossDonationsByEqId(eqid);
    }

    @GetMapping("/fromRedCrossDonations")
    public AjaxResult fromRedCrossDonations(@RequestParam("eqid") String eqid,
                                            @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<RedCrossDonations> redCrossDonationsList = redCrossDonationsService.fromRedCrossDonations(eqid, time);
        return AjaxResult.success(redCrossDonationsList);
    }


}
