package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import com.ruoyi.system.service.SecondaryDisasterInfoService;
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
public class SecondaryDisasterInfoController {

    @Autowired
    private SecondaryDisasterInfoService secondaryDisasterInfoService;



    @GetMapping("/secondarydisasterinfo")
    public List<SecondaryDisasterInfo> secondarydisasterinfo(@RequestParam String eqid) {
        return secondaryDisasterInfoService.SecondaryDisasterInfoByEqId(eqid);
    }

    @GetMapping("/fromSecondaryDisasterInfo")
    public AjaxResult fromSecondaryDisasterInfo(@RequestParam("eqid") String eqid,
                                                @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<SecondaryDisasterInfo> secondaryDisasterInfoList = secondaryDisasterInfoService.fromSecondaryDisasterInfo(eqid, time);
        return AjaxResult.success(secondaryDisasterInfoList);
    }

}
