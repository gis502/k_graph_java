package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.EmergencyRescueEquipment;
import com.ruoyi.system.service.impl.EmergencyRescueEquipmentServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emergencyResources")
public class EmergencyResourcesController {


    @Resource
    private EmergencyRescueEquipmentServiceImpl emergencyRescueEquipmentService;

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
