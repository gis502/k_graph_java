package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.RescueForces;
import com.ruoyi.system.service.RescueForcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class RescueForcesController {
    @Autowired
    private RescueForcesService rescueForcesService;



    @GetMapping("/rescueforces")
    public List<RescueForces> rescueforces(@RequestParam String eqid) {
        return rescueForcesService.RescueForcesByEqId(eqid);
    }
}
