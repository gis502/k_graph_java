package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import com.ruoyi.system.service.SecondaryDisasterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SecondaryDisasterInfoController {

    @Autowired
    private SecondaryDisasterInfoService secondaryDisasterInfoService;



    @GetMapping("/secondarydisasterinfo")
    public List<SecondaryDisasterInfo> secondarydisasterinfo(@RequestParam String eqid) {
        return secondaryDisasterInfoService.SecondaryDisasterInfoByEqId(eqid);
    }
}
