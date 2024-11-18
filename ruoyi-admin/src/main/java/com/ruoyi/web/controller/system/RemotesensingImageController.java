package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.OrthophotoImage;
import com.ruoyi.system.service.RemotesensingImageService;
import com.ruoyi.system.domain.entity.RemotesensingImage;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/remotesensing")
public class RemotesensingImageController {

    @Resource
    private RemotesensingImageService remotesensingImageService;

    //遥感影像---搜索
    @GetMapping("/queryRI")
    public AjaxResult queryRI(@RequestParam(value = "inputData", required = false) String inputData) {
        System.out.println("搜索的值"+ inputData);
        LambdaQueryWrapper<RemotesensingImage> wrapper = new LambdaQueryWrapper<>();
        if (inputData != null && !inputData.trim().isEmpty()) {
            wrapper
                    .like(RemotesensingImage::getName, inputData)
                    .or()
                    .like(RemotesensingImage::getPath, inputData)
                    .or()
                    .like(RemotesensingImage::getHeight, inputData)
                    .or()
                    .apply("TO_CHAR(create_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + inputData + "%")
                    .or()
                    .apply("CAST(angle AS TEXT) LIKE {0}", "%" + inputData + "%")
                    .or()
                    .apply("TO_CHAR(shooting_time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + inputData + "%");
        }
        List<RemotesensingImage> resultList = remotesensingImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }

    //遥感影像---增
    @PostMapping("/addRI")
    public AjaxResult addRI(@RequestBody RemotesensingImage remotesensingImage){  //使 JSON 数据自动映射到 RemotesensingImage 对象的字段中
        System.out.println("从前端传过来的数据:"+remotesensingImage);
        try {

            remotesensingImage.generateUuidIfNotPresent();
            return AjaxResult.success(remotesensingImageService.save(remotesensingImage));
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
            return AjaxResult.error("保存失败: " + e.getMessage());
        }

    }

    //遥感影像---删
    @DeleteMapping("/removeRI")
    public AjaxResult removeRI(@RequestParam(value = "uuid") String uuid) {
        return AjaxResult.success(remotesensingImageService.removeById(uuid));
    }

    //遥感影像---改
    @PostMapping("/updaRI")
    public AjaxResult updaRI(@RequestBody RemotesensingImage remotesensingImage) {
        System.out.println("改--从前端传过来的数据:"+remotesensingImage);
        return AjaxResult.success(remotesensingImageService.updateById(remotesensingImage));
    }

    //遥感影像---查
    @PostMapping("/searchRI")
    public AjaxResult searchRI(){
        System.out.println(remotesensingImageService.list());
        return AjaxResult.success(remotesensingImageService.list());
    }

    // 遥感影像---筛选
    @PostMapping("/filterRI")
    public AjaxResult filterRI(@RequestBody RemotesensingImage remotesensingImage) {
//        System.out.println("需要筛选的字段" + remotesensingImage);

        LambdaQueryWrapper<RemotesensingImage> wrapper = new LambdaQueryWrapper<>();
//        // 处理 `name` 字段  like 用于模糊查询
//        wrapper.like(
//                remotesensingImage.getName() != null && !remotesensingImage.getName().trim().isEmpty(),
//                RemotesensingImage::getName,
//                remotesensingImage.getName()
//        );
//
//        // 处理 `path` 字段  like 用于模糊查询   表示如果 name 字段不匹配，则继续检查 path 字段是否匹配。如果 path 也不匹配，则继续检查下一个条件，依此类推。
//        //如果俩个都用wrapper.like，就会生成and的SQL 语句
//        wrapper.or().like(
//                remotesensingImage.getPath() != null && !remotesensingImage.getPath().trim().isEmpty(),
//                RemotesensingImage::getPath,
//                remotesensingImage.getPath()
//        );


        // 名称字段筛选
        if (remotesensingImage.getName() != null && !remotesensingImage.getName().trim().isEmpty()) {
            wrapper.like(RemotesensingImage::getName, remotesensingImage.getName());
        }

        // 路径字段筛选
        if (remotesensingImage.getPath() != null && !remotesensingImage.getPath().trim().isEmpty()) {
            wrapper.like(RemotesensingImage::getPath, remotesensingImage.getPath());
        }

        // 处理 `height` 字段  eq 用于精确匹配
        if (remotesensingImage.getHeight() != null && !remotesensingImage.getHeight().trim().isEmpty()) {
            wrapper.eq(RemotesensingImage::getHeight, remotesensingImage.getHeight());
        }

        // 处理 `createTime` 字段 ge 表示大于或等于，用于范围查询，特别是处理日期或数值字段。
        if (remotesensingImage.getCreateTime() != null) {
            wrapper.ge(RemotesensingImage::getCreateTime, remotesensingImage.getCreateTime());
        }

        // 处理 `angle` 字段 eq 用于精确匹配
        if (remotesensingImage.getAngle() != null) {
//            wrapper.or().apply("CAST(angle AS TEXT) LIKE {0}", "%" + remotesensingImage.getAngle() + "%");
            wrapper.eq(RemotesensingImage::getAngle, remotesensingImage.getAngle());
        }

        // 处理 `shootingTime` 字段  ge 表示大于或等于，用于范围查询，特别是处理日期或数值字段。
        if (remotesensingImage.getShootingTime() != null) {
            wrapper.ge(RemotesensingImage::getShootingTime, remotesensingImage.getShootingTime());
        }

        List<RemotesensingImage> resultList = remotesensingImageService.list(wrapper);
        return AjaxResult.success(resultList);
    }

}






