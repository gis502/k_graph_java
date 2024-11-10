package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import com.ruoyi.system.domain.entity.SocialOrder;
import com.ruoyi.system.service.SocialOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/system")
public class SocialOrderController {

    @Autowired
    private SocialOrderService socialOrderService;

    @GetMapping("/socialorder")
    public List<SocialOrder> getsocialoption(@RequestParam String eqid) {
        return socialOrderService.getsocialoption(eqid);
    }
}
