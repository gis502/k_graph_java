package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.service.PublicOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

    @GetMapping("/fromPublicOpinion")
    public AjaxResult fromPublicOpinion(@RequestParam("eqid") String eqid,
                                        @RequestParam("time") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime time) {
        List<PublicOpinion> publicOpinionList = publicOpinionService.fromPublicOpinion(eqid, time);
        return AjaxResult.success(publicOpinionList);
    }


}
