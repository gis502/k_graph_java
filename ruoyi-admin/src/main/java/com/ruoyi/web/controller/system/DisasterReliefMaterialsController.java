package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.DisasterReliefMaterials;
import com.ruoyi.system.domain.entity.RescueForces;
import com.ruoyi.system.service.DisasterReliefMaterialsService;
import com.ruoyi.system.service.RescueForcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class DisasterReliefMaterialsController {
    @Autowired
    private DisasterReliefMaterialsService disasterReliefMaterialsService;



    @GetMapping("/disasterreliefmaterials")
    public List<DisasterReliefMaterials> disasterreliefmaterials(@RequestParam String eqid) {
        return disasterReliefMaterialsService.DisasterReliefMaterialsByEqId(eqid);
    }
}
