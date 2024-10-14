package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import com.ruoyi.system.service.PlotIconmanagementService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;


@RestController
@RequestMapping("/system/ploticon")
public class PlotIconmanagementController {

    @Resource
    private PlotIconmanagementService plotIconmanagementService;

    @PostMapping("/getploticon")
    public AjaxResult getploticon() {
        return AjaxResult.success(plotIconmanagementService.list());
    }

    @PostMapping("/deleteploticon/{uuid}")
    @Log(title = "标会图片管理", businessType = BusinessType.DELETE)
    public AjaxResult deletePlotIcon(@PathVariable("uuid") String id) {
        return AjaxResult.success(plotIconmanagementService.removeById(id));
    }

    @PostMapping("/updataploticon")
    @Log(title = "标会图片管理", businessType = BusinessType.UPDATE)
    public AjaxResult updataPlotIcon(@RequestBody PlotIconmanagement plotIcon) {
        return AjaxResult.success(plotIconmanagementService.updateById(plotIcon));
    }

    @PostMapping("/searchploticon")
    public List<PlotIconmanagement> searchPloticon(@RequestParam("menuName") String menuName) {
        QueryWrapper<PlotIconmanagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", menuName).or().like("describe", menuName).or().like("type", menuName);
        List<PlotIconmanagement> list = plotIconmanagementService.list(queryWrapper);
        return list;
    }

    @PostMapping("/addploticon")
    @Log(title = "标会图片管理", businessType = BusinessType.INSERT)
    public AjaxResult addPlotIcon(@RequestBody PlotIconmanagement plotIcon) {
        return AjaxResult.success(plotIconmanagementService.save(plotIcon));
    }
}
