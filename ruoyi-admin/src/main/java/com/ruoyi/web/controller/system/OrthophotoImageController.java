package com.ruoyi.web.controller.system;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.OrthophotoImage;
import com.ruoyi.system.service.OrthophotoImageService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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

    //搜索
    @GetMapping("/queryEq")
    public AjaxResult queryEq(@RequestParam(value = "queryValue") String queryValue) {
        LambdaQueryWrapper<OrthophotoImage> wrapper = new LambdaQueryWrapper<>();
        if (queryValue != null && !queryValue.trim().isEmpty()) {
            wrapper
                    .like(OrthophotoImage::getName, queryValue)
                    .or()
                    .like(OrthophotoImage::getPath, queryValue)
                    .or()
                    .like(OrthophotoImage::getHeight, queryValue)
                    .or()
                    .like(OrthophotoImage::getCreateTime, queryValue)
                    .or()
                    .like(OrthophotoImage::getAngle, queryValue);
        }
        List<OrthophotoImage> resultList = orthophotoImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }


    //筛选
    @PostMapping("/fromeq")
    public AjaxResult fromeq(@RequestBody OrthophotoImage orthophotoImage) {
        LambdaQueryWrapper<OrthophotoImage> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(orthophotoImage.getName() != null && !orthophotoImage.getName().trim().isEmpty(), OrthophotoImage::getName, orthophotoImage.getName())
                .or()
                .like(orthophotoImage.getPath() != null && !orthophotoImage.getPath().trim().isEmpty(), OrthophotoImage::getPath, orthophotoImage.getPath())
                .or()
                .like(orthophotoImage.getHeight() != null && !orthophotoImage.getHeight().trim().isEmpty(), OrthophotoImage::getHeight, orthophotoImage.getHeight())
                .or()
                .ge(orthophotoImage.getCreateTime() != null, OrthophotoImage::getCreateTime, orthophotoImage.getCreateTime())
                .or()
                .like(orthophotoImage.getAngle() != null, OrthophotoImage::getAngle, orthophotoImage.getAngle());

        List<OrthophotoImage> resultList = orthophotoImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }

}

