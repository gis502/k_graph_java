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
    public AjaxResult queryEq(@RequestParam(value = "queryValue", required = false) String queryValue) {
        System.out.println("啊啊啊啊啊啊啊啊啊啊啊"+queryValue);
        LambdaQueryWrapper<OrthophotoImage> wrapper = new LambdaQueryWrapper<>();
        if (queryValue != null && !queryValue.trim().isEmpty()) {
            wrapper
                    .like(OrthophotoImage::getName, queryValue)
                    .or()
                    .like(OrthophotoImage::getPath, queryValue)
                    .or()
                    .like(OrthophotoImage::getHeight, queryValue)
                    .or()
                    .apply("TO_CHAR(create_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%")
                    .or()
                    .apply("CAST(angle AS TEXT) LIKE {0}", "%" + queryValue + "%");
        }
        List<OrthophotoImage> resultList = orthophotoImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }


    //筛选
    @PostMapping("/fromeq")
    public AjaxResult fromeq(@RequestBody OrthophotoImage orthophotoImage) {
        System.out.println("哈哈哈哈哈哈哈哈哈哈" + orthophotoImage);
        LambdaQueryWrapper<OrthophotoImage> wrapper = new LambdaQueryWrapper<>();
        // 处理字符串类型字段
        wrapper.like(
                orthophotoImage.getName() != null && !orthophotoImage.getName().trim().isEmpty(),
                OrthophotoImage::getName,
                orthophotoImage.getName()
        );
        wrapper.or().like(
                orthophotoImage.getPath() != null && !orthophotoImage.getPath().trim().isEmpty(),
                OrthophotoImage::getPath,
                orthophotoImage.getPath()
        );
        // 处理 height 字段，假设是字符串类型
        if (orthophotoImage.getHeight() != null && !orthophotoImage.getHeight().trim().isEmpty()) {
            wrapper.or().like(OrthophotoImage::getHeight, orthophotoImage.getHeight());
        }
        // 处理 create_time 字段，使用范围查询
        if (orthophotoImage.getCreateTime() != null) {
            wrapper.or().ge(OrthophotoImage::getCreateTime, orthophotoImage.getCreateTime());
        }

        // 处理 angle 字段，假设是数值类型
        if (orthophotoImage.getAngle() != null) {
            wrapper.or().apply("CAST(angle AS TEXT) LIKE {0}", "%" + orthophotoImage.getAngle() + "%");
        }

        List<OrthophotoImage> resultList = orthophotoImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }

}

