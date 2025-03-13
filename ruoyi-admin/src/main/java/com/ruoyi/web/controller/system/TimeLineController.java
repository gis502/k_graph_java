package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.dto.PlotRequest;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.service.impl.EmergencyResponseInfoServiceImpl;
import com.ruoyi.system.service.impl.NewsServiceImpl;
import com.ruoyi.system.service.impl.RescueActionCasualtiesServiceImpl;
import com.ruoyi.system.service.impl.RescueTeamServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/timeLine")
public class TimeLineController {

    @Resource
    private EmergencyResponseInfoServiceImpl emergencyResponseInfoService;

    @Resource
    private RescueActionCasualtiesServiceImpl rescueActionCasualtiesService;

    @Resource
    private RescueTeamServiceImpl rescueTeamService;

    @Resource
    private NewsServiceImpl newsService;

    // ----应急响应
    @GetMapping("/emergencyResponse")
    public List<EmergencyResponseInfo> getEmergencyResponse(@RequestParam("eqid") String eqid) {
        return emergencyResponseInfoService.getByEqid(eqid);
    }

    @PostMapping("/saveEmergencyResponse")
    public boolean saveEmergencyResponse(@RequestBody EmergencyResponseInfo data) {
//        System.out.println("saveEmergencyResponse"+data);
        return emergencyResponseInfoService.save(data);
    }
    // ----人员伤亡
    @GetMapping("/rescueActionCasualties")
    public List<RescueActionCasualties> getRescueActionCasualties(@RequestParam("eqid") String eqid) {

        // 根据传递的 eqid 参数查询数据
        return rescueActionCasualtiesService.getByEqid(eqid);
    }

    // ----救援出队
    @GetMapping("/rescueTeam")
    public List<RescueTeam> getRescueTeam(@RequestParam("eqid") String eqid) {
        return rescueTeamService.getByEqid(eqid);
    }
    //    public List<RescueTeam_timeInnerJoin> getRescueTeam(@RequestParam("eqid") String eqid) {
    //        return rescueTeamService.getInfoByEqid(eqid);
    //}

    // ----最新新闻
    @GetMapping("/news")
    public List<News> getNews(@RequestParam("eqid") String eqid) {
        return newsService.getByEqid(eqid);
    }





}
