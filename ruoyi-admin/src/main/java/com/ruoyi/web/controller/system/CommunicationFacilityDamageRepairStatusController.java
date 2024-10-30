package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.service.CommunicationFacilityDamageRepairStatusService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class CommunicationFacilityDamageRepairStatusController {
    @Autowired
    private CommunicationFacilityDamageRepairStatusService communicationFacilityDamageRepairStatusService;

    @GetMapping("/facility")
    public List<CommunicationFacilityDamageRepairStatus> facility(@RequestParam String eqid){
        return communicationFacilityDamageRepairStatusService.facility(eqid);
    }
}
