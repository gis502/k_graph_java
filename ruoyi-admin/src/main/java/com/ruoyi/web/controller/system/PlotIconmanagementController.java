package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import com.ruoyi.system.service.PlotIconmanagementService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/system/ploticon")
public class PlotIconmanagementController {

    @Resource
    private PlotIconmanagementService plotIconmanagementService;

    @PostMapping("/getploticon")
    public AjaxResult getploticon(){
        return AjaxResult.success(plotIconmanagementService.list());
    }
    @PostMapping("/deleteploticon/{uuid}")
    public AjaxResult deletePlotIcon(@PathVariable("uuid") String id){
        return AjaxResult.success(plotIconmanagementService.removeById(id));
    }
    @PostMapping("/updataploticon")
    public AjaxResult updataPlotIcon(@RequestBody PlotIconmanagement plotIcon){
        return AjaxResult.success(plotIconmanagementService.updateById(plotIcon));
    }
    @PostMapping("/addploticon")
    public AjaxResult addPlotIcon(@RequestBody PlotIconmanagement plotIcon){
        return AjaxResult.success(plotIconmanagementService.save(plotIcon));
    }
}
