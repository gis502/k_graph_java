package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.entity.ListTable;
import com.ruoyi.system.domain.entity.SafetyProtection;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.SafetyProtectionService;
import com.ruoyi.web.controller.common.RequestParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMethod;
@RestController
@RequestMapping("/safety_protection")
public class SafetyProtectionController {

    @Autowired
    private SafetyProtectionService safetyProtectionService;

    /**
     * 增
     */
    @PostMapping("/insert")
    public AjaxResult insert(@RequestBody SafetyProtection safetyProtection) {
        return AjaxResult.success(safetyProtectionService.save(safetyProtection));
    }

    /**
     * 分页查
     */
    @RequestMapping(value = "getSafetyProtection", method = {RequestMethod.POST, RequestMethod.GET})
    public Page<SafetyProtection> page(@RequestBody RequestParams requestParams) {
        Page<SafetyProtection> page = new Page<>(requestParams.getPageNum(), requestParams.getPageSize());
        LambdaQueryWrapper<SafetyProtection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SafetyProtection::getApplicationType, requestParams.getQueryValue())
                .or().like(SafetyProtection::getNotes, requestParams.getQueryValue());
        return safetyProtectionService.page(page, queryWrapper);
    }

    /**
     * 刪
     */
    @DeleteMapping("/removeById")
    public boolean removeById(@RequestParam(value = "id") String id) {
        return safetyProtectionService.removeById(id);
    }

    /**
     * 改
     */
    @PutMapping("/update")
    public boolean update(@RequestBody SafetyProtection safetyProtection ) {
        return safetyProtectionService.updateById(safetyProtection);
    }




}


