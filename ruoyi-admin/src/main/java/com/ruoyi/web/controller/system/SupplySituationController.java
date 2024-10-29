package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.entity.SupplySituation;
import com.ruoyi.system.service.SupplySituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SupplySituationController {
    @Autowired
    private SupplySituationService supplySituationService;

    @GetMapping("/supplySituationList")
    public List<SupplySituation> supplySituationList(@RequestParam(required = false) String eqid) {
        return supplySituationService.getsupplySituationById(eqid);
    }
}
