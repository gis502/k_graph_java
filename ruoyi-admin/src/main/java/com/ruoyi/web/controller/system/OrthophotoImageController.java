package com.ruoyi.web.controller.system;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.system.domain.entity.OrthophotoImage;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import com.ruoyi.system.service.OrthophotoImageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/orthophoto")
public class OrthophotoImageController {
    @Resource
    private OrthophotoImageService orthophotoImageService;

    //增
    @PostMapping("/save")
    public AjaxResult save(@RequestBody OrthophotoImage orthophotoImage) {

        System.out.println("从前端传过来的数据:" + orthophotoImage);
        try {
            orthophotoImage.generateUuidIfNotPresent();
            return AjaxResult.success(orthophotoImageService.save(orthophotoImage));
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return AjaxResult.error("保存失败: " + e.getMessage());
        }
    }

    //删
    @DeleteMapping("/removeById")
    public AjaxResult removeById(@RequestParam(value = "uuid") String uuid) {
        return AjaxResult.success(orthophotoImageService.removeById(uuid));
    }

    //改
    @PostMapping("/update")
    public AjaxResult updata(@RequestBody OrthophotoImage orthophotoImage) {
        return AjaxResult.success(orthophotoImageService.updateById(orthophotoImage));
    }

    //查
    @PostMapping("/list")
    public AjaxResult list() {
        System.out.println(orthophotoImageService.list());
        return AjaxResult.success(orthophotoImageService.list());
    }


    @PostMapping("/page")
    public AjaxResult page(@RequestBody PageDomain pageDomain) {
        Page<OrthophotoImage> page = new Page<>(pageDomain.getPageNum(), pageDomain.getPageSize());
        LambdaQueryWrapper<OrthophotoImage> wrapper = new LambdaQueryWrapper<>();
        Page<OrthophotoImage> resultPage = orthophotoImageService.page(page, wrapper);
        return AjaxResult.success(resultPage);
    }
}


