package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import com.ruoyi.system.service.PlotIconmanagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/system/ploy")
public class PlotIconmanagementController {

    @Resource
    private PlotIconmanagementService plotIconmanagementService;

    @PostMapping("/getploticon")
    public AjaxResult getploticon(){
        return AjaxResult.success(plotIconmanagementService.list());

    }
}