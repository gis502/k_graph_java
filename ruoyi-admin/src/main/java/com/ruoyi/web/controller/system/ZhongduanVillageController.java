package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.ZhongduanVillage;
import com.ruoyi.system.service.ZhongduanVillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class ZhongduanVillageController {

    @Autowired
    private ZhongduanVillageService zhongduanVillageService;

    @GetMapping("/village")
    public List<ZhongduanVillage> villages(@RequestParam String eqid){
        return zhongduanVillageService.getVillageByEqid(eqid);
    }
}
