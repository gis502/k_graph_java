package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.service.PopulationDataService;
import net.bytebuddy.asm.Advice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/system/populationdata")
public class PopulationDataController {
    @Resource PopulationDataService populationDataService;
    @PostMapping("/list")
    public AjaxResult List(){
        return AjaxResult.success(populationDataService.listall());
    }
}
