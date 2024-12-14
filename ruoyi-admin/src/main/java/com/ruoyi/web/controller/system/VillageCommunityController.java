package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.entity.VillageCommunity;
import com.ruoyi.system.mapper.VillageCommunityMapper;
import com.ruoyi.system.service.VillageCommunityService;
import com.ruoyi.system.service.impl.VillageCommunityServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/villageCommunity")
public class VillageCommunityController {

    @Resource
    private VillageCommunityServiceImpl villageCommunityService;

    @Resource
    private VillageCommunityMapper villageCommunityMapper;


    @PostMapping("getNearbyVillage")
    public List<VillageCommunity> getNearbyVillage(@RequestBody Map<String,Object> position){

        System.out.println("position-------------------------------------"+position);

        // 获取传入的经纬度信息
        double longitude = Double.parseDouble(position.get("longitude").toString());
        double latitude = Double.parseDouble(position.get("latitude").toString());

        // 查询附近 20 公里范围内的村庄
        return villageCommunityMapper.findNearbyVillages(longitude, latitude);
    }

}
