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
        LambdaQueryWrapper<Tiltphotographymodel> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(tiltphotographymodel.getName() != null && !tiltphotographymodel.getName().trim().isEmpty(), Tiltphotographymodel::getName, tiltphotographymodel.getName())
                .or()
                .like(tiltphotographymodel.getPath() != null && !tiltphotographymodel.getPath().trim().isEmpty(), Tiltphotographymodel::getPath, tiltphotographymodel.getPath())
                .or()
                .like(tiltphotographymodel.getRz() != null, Tiltphotographymodel::getRz, tiltphotographymodel.getRz())
                .or()
                .like(tiltphotographymodel.getTz() != null, Tiltphotographymodel::getTz, tiltphotographymodel.getTz())
                .or()
                .like(tiltphotographymodel.getTime() != null, Tiltphotographymodel::getTime, tiltphotographymodel.getTime())
                .or()
                .like(tiltphotographymodel.getRze() != null, Tiltphotographymodel::getRze, tiltphotographymodel.getRze())
                .or()
                .like(tiltphotographymodel.getTze() != null, Tiltphotographymodel::getTze, tiltphotographymodel.getTze())
                .or()
                .like(tiltphotographymodel.getModelSize() != null, Tiltphotographymodel::getModelSize, tiltphotographymodel.getModelSize());
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
                    .like(Tiltphotographymodel::getRz, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getTz, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getTime, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getRze, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getTze, queryValue)
                    .or()
                    .like(Tiltphotographymodel::getModelSize, queryValue);
        }
        List<Tiltphotographymodel> resultList = tiltphotographymodelService.list(wrapper);
        return AjaxResult.success(resultList);
    }

}
