package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.entity.Tiltphotographymodel;
import com.ruoyi.system.service.TiltphotographymodelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/system/model")
public class TiltPhotographyModelController {

    @Autowired
    private TiltphotographymodelService tiltphotographymodelService;

    @GetMapping("/getmodel")
    public List<Tiltphotographymodel> selectAllModel() {
        List<Tiltphotographymodel> data = tiltphotographymodelService.selectAllModel();
        return data;
    }


    @GetMapping("/getModelTotalCount")
    public int getModelTotalCount() {
        return tiltphotographymodelService.getModelTotalCount();
    }

    @GetMapping("/deletemodel")
    public int deleteModel(String uuid) {
        int data = tiltphotographymodelService.deleteModel(uuid);
        return data;
    }


    @PostMapping("/addmodel")
    public int addModel(@RequestBody Tiltphotographymodel model) {
        int data = tiltphotographymodelService.addModel(model);
        return data;
    }

    @PostMapping("/updatamodel")
    public int updataModel(@RequestBody Tiltphotographymodel model) {
        int data = tiltphotographymodelService.updataModel(model);
        return data;
    }

    @PostMapping("/updatamodels")
    public int updataModels(@RequestBody Tiltphotographymodel model) {
        System.out.println("update" + model);
        int data = tiltphotographymodelService.updataModels(model);
        return data;
    }


    @PostMapping("/updatamodelnoelevation")
    public int updataModelNoElevation(@RequestBody Tiltphotographymodel model) {
        int data = tiltphotographymodelService.updataModelNoElevation(model);
        return data;
    }

    @PostMapping("/updatamodelelevation")
    public int updataModelElevation(@RequestBody Tiltphotographymodel model) {
        int data = tiltphotographymodelService.updataModelElevation(model);
        return data;
    }


    //筛选
    @PostMapping("/fromeq")
    public AjaxResult fromeq(@RequestBody Tiltphotographymodel tiltphotographymodel) {
        System.out.println("哈哈哈哈哈哈哈哈" + tiltphotographymodel);
        LambdaQueryWrapper<Tiltphotographymodel> wrapper = new LambdaQueryWrapper<>();

        // 处理字符串类型字段
        wrapper.like(
                tiltphotographymodel.getName() != null && !tiltphotographymodel.getName().trim().isEmpty(),
                Tiltphotographymodel::getName,
                tiltphotographymodel.getName()
        );
        wrapper.like(
                tiltphotographymodel.getPath() != null && !tiltphotographymodel.getPath().trim().isEmpty(),
                Tiltphotographymodel::getPath,
                tiltphotographymodel.getPath()
        );

        // 处理数值类型字段，使用 `=` 或将其转换为字符串
        if (tiltphotographymodel.getRz() != null) {
            wrapper.apply("CAST(rz AS TEXT) LIKE {0}", "%" + tiltphotographymodel.getRz() + "%");
        }
        if (tiltphotographymodel.getTz() != null) {
            wrapper.apply("CAST(tz AS TEXT) LIKE {0}", "%" + tiltphotographymodel.getTz() + "%");
        }

        // 处理时间字段
        if (tiltphotographymodel.getTime() != null) {
            // 将 LocalDateTime 视为 UTC 时间并转换为本地时区
            ZonedDateTime utcTime = tiltphotographymodel.getTime().atZone(ZoneId.of("UTC"));
            ZonedDateTime localTime = utcTime.withZoneSameInstant(ZoneId.systemDefault());

            // 格式化为数据库中匹配的格式字符串
            String formattedTime = localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 将格式化后的时间应用到查询中
            wrapper.apply("TO_CHAR(time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + formattedTime + "%");
        }

        // 处理其他数值类型字段
        if (tiltphotographymodel.getRze() != null) {
            wrapper.apply("CAST(rze AS TEXT) LIKE {0}", "%" + tiltphotographymodel.getRze() + "%");
        }
        if (tiltphotographymodel.getTze() != null) {
            wrapper.apply("CAST(tze AS TEXT) LIKE {0}", "%" + tiltphotographymodel.getTze() + "%");
        }
        if (tiltphotographymodel.getModelSize() != null) {
            wrapper.apply("CAST(model_size AS TEXT) LIKE {0}", "%" + tiltphotographymodel.getModelSize() + "%");
        }

        List<Tiltphotographymodel> resultList = tiltphotographymodelService.list(wrapper);
        return AjaxResult.success(resultList);
    }

    //搜索
    @GetMapping("/queryEq")
    public AjaxResult queryEq(@RequestParam(value = "queryValue", required = false) String queryValue) {
        LambdaQueryWrapper<Tiltphotographymodel> wrapper = new LambdaQueryWrapper<>();
        if (queryValue != null && !queryValue.trim().isEmpty()) {
            wrapper
                    .like(Tiltphotographymodel::getName, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getPath, queryValue)
                    .or()
                    .apply("CAST(rz AS TEXT) LIKE {0}", "%" + queryValue + "%") // 将 rz 转换为字符串
                    .or()
                    .apply("CAST(tz AS TEXT) LIKE {0}", "%" + queryValue + "%") // 将 tz 转换为字符串
                    .or()
                    .apply("TO_CHAR(time, 'YYYY-MM-DD HH24:MI:SS') LIKE {0}", "%" + queryValue + "%") // 格式化时间
                    .or()
                    .apply("CAST(rze AS TEXT) LIKE {0}", "%" + queryValue + "%") // 将 rze 转换为字符串
                    .or()
                    .apply("CAST(tze AS TEXT) LIKE {0}", "%" + queryValue + "%") // 将 tze 转换为字符串
                    .or()
                    .apply("CAST(model_size AS TEXT) LIKE {0}", "%" + queryValue + "%"); // 将 modelSize 转换为字符串
        }

        List<Tiltphotographymodel> resultList = tiltphotographymodelService.list(wrapper);
        return AjaxResult.success(resultList);
    }

}
