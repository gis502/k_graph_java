package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.DisasterReliefSupplies;
import com.ruoyi.system.domain.entity.EmergencyRescueEquipment;
import com.ruoyi.system.domain.entity.EmergencyShelters;
import com.ruoyi.system.domain.entity.RescueTeamsInfo;
import com.ruoyi.system.service.impl.DisasterReliefSuppliesServiceImpl;
import com.ruoyi.system.service.impl.EmergencyRescueEquipmentServiceImpl;
import com.ruoyi.system.service.impl.EmergencySheltersServiceImpl;
import com.ruoyi.system.service.impl.RescueTeamsInfoServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private EmergencySheltersServiceImpl emergencySheltersService;

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

    // ----救援队伍页面----
    @GetMapping("/rescueTeamList")
    public List<RescueTeamsInfo> rescueTeamList() {
        return rescueTeamsInfoService.list();
    }

    //    救援队伍页面--搜索功能 介绍：<级别名称、地址、负责人、负责人电话、数据来源、组织结构、队伍类型名称>等等搜索具体的内容；<总人数>数量字段，仅搜索字段名且字段数据数量>0
    @PostMapping("/searchEmergencyTeam")
    public List<RescueTeamsInfo> searchEmergencyTeam(@RequestParam("inputData") String inputData) {
        QueryWrapper<RescueTeamsInfo> queryWrapper = new QueryWrapper<>();

        // 1. 文本字段的模糊搜索
        queryWrapper.or(wrapper -> wrapper
                .like("affiliated_agency", inputData)
                .or().like("level_name", inputData)
                .or().like("team_type_name", inputData)
                .or().like("address", inputData)
                .or().like("main_responsibilities", inputData)
                .or().like("expertise_description", inputData)
                .or().like("emergency_communication_methods", inputData)
                .or().like("assembly_location", inputData)
                .or().like("self_transportation", inputData)
                .or().like("person_in_charge", inputData)
                .or().like("charge_phone", inputData)
                .or().like("confidentiality_name", inputData)
                .or().like("modifier_name", inputData)
                .or().like("qualification_level", inputData)
                .or().like("data_source", inputData)
                // 使用 PostgreSQL 的 TO_CHAR 函数将日期字段转换为字符串
                .or().apply("TO_CHAR(establishment_date, 'YYYY-MM-DD') LIKE {0}", "%" + inputData + "%")
                .or().apply("TO_CHAR(preparation_time, 'YYYY-MM-DD') LIKE {0}", "%" + inputData + "%")
        );

        // 查询文本字段匹配的数据
        List<RescueTeamsInfo> resultList = rescueTeamsInfoService.list(queryWrapper);

        // 2.如果文本字段查询未匹配到任何结果，进行数量字段的匹配
        if (resultList.isEmpty()) {
            // 数量字段中文名称与数据库字段名的映射关系
            Map<String, String> fieldMapping = new HashMap<>();
            fieldMapping.put("总人数", "total_personnel");

            // 检查输入数据是否匹配数量字段
            String fieldName = fieldMapping.get(inputData);
            if (fieldName != null) {
                // 创建新的查询条件，仅查询数量字段大于等于1的数据
                queryWrapper = new QueryWrapper<>();
                queryWrapper.ge(fieldName, 1);
                resultList = rescueTeamsInfoService.list(queryWrapper);
                System.out.println("resultList"+resultList);
            } else {
                // 如果没有找到匹配的数量字段，则清空结果列表
                resultList = new ArrayList<>();
            }
        }
        return resultList;
    }

    // ----避难场所----
    @GetMapping("/sheltersList")
    public List<EmergencyShelters> sheltersList() {
        return emergencySheltersService.list();
    }

    //    避难场所页面--搜索功能 介绍：<名字、地址、负责人、负责人电话、数据来源>等等搜索具体的内容；<容纳人数>数量字段，仅搜索字段名且字段数据数量>0
    @PostMapping("/searchEmergencyShelters")
    public List<EmergencyShelters> searchEmergencyShelters(@RequestParam("inputData") String inputData) {
        QueryWrapper<EmergencyShelters> queryWrapper = new QueryWrapper<>();

        // 1. 文本字段的模糊搜索
        queryWrapper.or(wrapper -> wrapper
                .like("name", inputData)
                .or().like("shelter_type_name", inputData)
                // 数字字段 area 的类型是numeric，需要使用 apply 转换为文本后进行匹配
                // PostgreSQL 不支持使用 LIKE 操作符来比较 numeric 类型和 character varying 类型的数据
                .or().apply("CAST(area AS TEXT) LIKE {0}", "%" + inputData + "%") // 将 area 转换为文本
                .or().apply("CAST(capacity AS TEXT) LIKE {0}", "%" + inputData + "%") // 将 capacity 转换为文本
                .or().like("level_name", inputData)
                .or().like("secrecy_level", inputData)
                .or().like("administrative_division", inputData)
                .or().like("address", inputData)
                .or().like("person_in_charge", inputData)
                .or().like("emergency_phone", inputData)
                .or().like("emergency_mobile", inputData)
                .or().like("affiliated_organization", inputData)
                .or().apply("CAST(design_service_life AS TEXT) LIKE {0}", "%" + inputData + "%") // 将 design_usage_years 转换为文本
                .or().like("description", inputData)
                .or().like("seismic_intensity", inputData)
                .or().like("data_source_unit", inputData)
                .or().like("remarks", inputData)
                // 使用 PostgreSQL 的 TO_CHAR 函数将日期字段转换为字符串
                .or().apply("TO_CHAR(start_time, 'YYYY-MM-DD') LIKE {0}", "%" + inputData + "%")
        );

        // 查询文本字段匹配的数据
        List<EmergencyShelters> resultList = emergencySheltersService.list(queryWrapper);

        // 2.如果文本字段查询未匹配到任何结果，进行数量字段的匹配
        if (resultList.isEmpty()) {
            // 数量字段中文名称与数据库字段名的映射关系
            Map<String, String> fieldMapping = new HashMap<>();
            fieldMapping.put("容纳人数", "capacity");

            // 检查输入数据是否匹配数量字段
            String fieldName = fieldMapping.get(inputData);
            if (fieldName != null) {
                // 创建新的查询条件，仅查询数量字段大于等于1的数据
                queryWrapper = new QueryWrapper<>();
                queryWrapper.ge(fieldName, 1);
                resultList = emergencySheltersService.list(queryWrapper);
                System.out.println("resultList"+resultList);
            } else {
                // 如果没有找到匹配的数量字段，则清空结果列表
                resultList = new ArrayList<>();
            }
        }
        return resultList;
    }

}
