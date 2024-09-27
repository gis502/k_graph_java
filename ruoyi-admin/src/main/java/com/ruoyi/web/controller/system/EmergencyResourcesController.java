package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.DisasterReliefSupplies;
import com.ruoyi.system.domain.entity.EmergencyRescueEquipment;
import com.ruoyi.system.domain.entity.RescueTeamsInfo;
import com.ruoyi.system.service.impl.DisasterReliefSuppliesServiceImpl;
import com.ruoyi.system.service.impl.EmergencyRescueEquipmentServiceImpl;
import com.ruoyi.system.service.impl.RescueTeamsInfoServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emergencyResources")
public class EmergencyResourcesController {

    @Resource
    private EmergencyRescueEquipmentServiceImpl emergencyRescueEquipmentService;

    @Resource
    private RescueTeamsInfoServiceImpl rescueTeamsInfoService;

    @Resource
    private DisasterReliefSuppliesServiceImpl disasterReliefSuppliesService;

    @GetMapping("/getEmergency")
    public Map<String, List<?>> getEmergency() {
        // 创建 QueryWrapper 来过滤掉 geom 为 null 的数据
        QueryWrapper<EmergencyRescueEquipment> equipmentQuery = new QueryWrapper<>();
        equipmentQuery.isNotNull("geom");
        List<EmergencyRescueEquipment> emergencyRescueEquipmentList = emergencyRescueEquipmentService.list(equipmentQuery);

        QueryWrapper<RescueTeamsInfo> teamsQuery = new QueryWrapper<>();
        teamsQuery.isNotNull("geom");
        List<RescueTeamsInfo> rescueTeamsInfoList = rescueTeamsInfoService.list(teamsQuery);

        QueryWrapper<DisasterReliefSupplies> suppliesQuery = new QueryWrapper<>();
        suppliesQuery.isNotNull("geom");
        List<DisasterReliefSupplies> disasterReliefSuppliesList = disasterReliefSuppliesService.list(suppliesQuery);

//        // 解析 geom 字段，转换为经纬度
//        emergencyRescueEquipmentList.forEach(EmergencyRescueEquipment::parseGeom);
//        rescueTeamsInfoList.forEach(RescueTeamsInfo::parseGeom);
//        disasterReliefSuppliesList.forEach(DisasterReliefSupplies::parseGeom);

        // 存入 Map
        Map<String, List<?>> emergencyData = new HashMap<>();
        emergencyData.put("emergencyRescueEquipment", emergencyRescueEquipmentList);
        emergencyData.put("rescueTeamsInfo", rescueTeamsInfoList);
        emergencyData.put("disasterReliefSupplies", disasterReliefSuppliesList);

        return emergencyData; // 返回包含三个列表的 Map
    }

    @PostMapping("/searchMaterialData")
    public List<EmergencyRescueEquipment> searchMaterialData(@RequestBody Map<String,Object> inputData){
        String country = (String) inputData.get("country");
        String address = (String) inputData.get("address");
        String contactPerson = (String) inputData.get("contactPerson");
        String contactPhone = (String) inputData.get("contactPhone");
        QueryWrapper<EmergencyRescueEquipment> queryWrapper = new QueryWrapper<>();
        if(!country.equals("")){
            queryWrapper.like("country",country);
        }
        if(!address.equals("")){
            queryWrapper.like("address",address);
        }
        if(!contactPerson.equals("")){
            queryWrapper.like("contact_person",contactPerson);
        }
        if(!contactPhone.equals("")){
            queryWrapper.like("contact_phone",contactPhone);
        }
        return emergencyRescueEquipmentService.list(queryWrapper);
    }

}
