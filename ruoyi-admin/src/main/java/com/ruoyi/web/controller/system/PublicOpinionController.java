package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.service.PublicOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system")
public class PublicOpinionController {

    @Autowired
    private PublicOpinionService publicOpinionService;

    @GetMapping("/publicopinion")
    public List<PublicOpinion> getpublicopinion(@RequestParam String eqid) {
        return publicOpinionService.getpublicopinion(eqid);
    }

}
