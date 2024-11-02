package com.ruoyi.web.controller.system;

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
    public List<Tiltphotographymodel> selectAllModel(){
        List<Tiltphotographymodel> data = tiltphotographymodelService.selectAllModel();
        return data;
    }


    @GetMapping("/getModelTotalCount")
    public int getModelTotalCount(){
        return tiltphotographymodelService.getModelTotalCount();
    }

    @GetMapping("/deletemodel")
    public int deleteModel(String uuid){
        int data = tiltphotographymodelService.deleteModel(uuid);
        return data;
    }


    @PostMapping("/addmodel")
    public int addModel(@RequestBody Tiltphotographymodel model){
        int data = tiltphotographymodelService.addModel(model);
        return data;
    }
    @PostMapping("/updatamodel")
    public int updataModel(@RequestBody Tiltphotographymodel model){
        int data = tiltphotographymodelService.updataModel(model);
        return data;
    }

    @PostMapping("/updatamodels")
    public int updataModels(@RequestBody Tiltphotographymodel model){
        System.out.println("update" + model);
        int data = tiltphotographymodelService.updataModels(model);
        return data;
    }


    @PostMapping("/updatamodelnoelevation")
    public int updataModelNoElevation(@RequestBody Tiltphotographymodel model){
        int data = tiltphotographymodelService.updataModelNoElevation(model);
        return data;
    }
    @PostMapping("/updatamodelelevation")
    public int updataModelElevation(@RequestBody Tiltphotographymodel model){
        int data = tiltphotographymodelService.updataModelElevation(model);
        return data;
    }



}
