package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.service.YaanJsonService;
import com.ruoyi.system.service.impl.DisasterReliefSuppliesServiceImpl;
import com.ruoyi.system.service.impl.EmergencyRescueEquipmentServiceImpl;
import com.ruoyi.system.service.impl.EmergencySheltersServiceImpl;
import com.ruoyi.system.service.impl.RescueTeamsInfoServiceImpl;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

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

    @Resource
    private YaanJsonService yaanJsonService;

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

    // 物资查询
    @PostMapping("/searchMaterialData")
    public List<DisasterReliefSupplies> searchMaterialData(@RequestBody Map<String,Object> inputData){
        String county = (String) inputData.get("county");
        String address = (String) inputData.get("address");
        String contactPerson = (String) inputData.get("contactPerson");
        String contactPhone = (String) inputData.get("contactPhone");
        QueryWrapper<DisasterReliefSupplies> queryWrapper = new QueryWrapper<>();
        if(!county.equals("")){
            queryWrapper.like("county",county);
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
        queryWrapper.isNotNull("geom");
        return disasterReliefSuppliesService.list(queryWrapper);
    }

    // 救援力量查询
    @PostMapping("/searchEmergencyTeamData")
    public List<RescueTeamsInfo> searchEmergencyTeamData(@RequestBody Map<String,Object> inputData){
        String levelName = (String) inputData.get("levelName");
        String teamTypeName = (String) inputData.get("teamTypeName");
        Integer totalMembers = (Integer) inputData.get("totalMembers");
        String address = (String) inputData.get("address");
        String personInCharge = (String) inputData.get("personInCharge");
        String chargePhone = (String) inputData.get("chargePhone");
        QueryWrapper<RescueTeamsInfo> queryWrapper = new QueryWrapper<>();
        if(!levelName.equals("")){
            queryWrapper.like("level_name",levelName);
        }
        if(!address.equals("")){
            queryWrapper.like("address",address);
        }
        if(!teamTypeName.equals("")){
            queryWrapper.like("team_type_name",teamTypeName);
        }
        if(!totalMembers.equals("")){
            queryWrapper.gt("total_members",totalMembers);
        }
        if(!personInCharge.equals("")){
            queryWrapper.like("person_in_charge",personInCharge);
        }
        if(!chargePhone.equals("")){
            queryWrapper.like("charge_phone",chargePhone);
        }
        queryWrapper.isNotNull("geom");
        return rescueTeamsInfoService.list(queryWrapper);
    }


    // --------首页面：应急物资、救援队伍、避难场所的要素图层-------
    @GetMapping("/getFeaturesLayer")
    public Map<String, List<?>> getFeaturesLayer() {
        List<DisasterReliefSupplies> disasterReserves = disasterReliefSuppliesService.list()
                .stream()
//        过滤数据时同时排除 null 值和 "0.00000000" 的值
                .filter(it -> it.getLongitude() != null && !it.getLongitude().equals("0.00000000"))
                .collect(Collectors.toList());

        List<EmergencyShelters> emergencyShelters = emergencySheltersService.list()
                .stream()
//        过滤数据时同时排除 null 值和 "0.00000000" 的值
                .filter(it -> it.getLongitude() != null && !it.getLongitude().equals("0.00000000"))
                .collect(Collectors.toList());

        // --------救援装备、急物资存储、避难场所、救援队伍-------
        // ----救援装备表----
        List<RescueTeamsInfo> emergencyTeam = rescueTeamsInfoService.list()
                .stream()
//        过滤数据时同时排除 null 值和 "0.00000000" 的值
                .filter(it -> it.getLongitude() != null && !it.getLongitude().equals("0.00000000"))
                .collect(Collectors.toList());

        // Map 是一种键值对集合，用于存储和快速查找数据
        Map<String, List<?>> emergencyData = new HashMap<>();
        emergencyData.put("disasterReserves", disasterReserves);
        emergencyData.put("emergencyShelters", emergencyShelters);
        emergencyData.put("emergencyTeam", emergencyTeam);
//        System.out.println(emergencyData); 检查是否获得数据，已获得！
        return emergencyData; //该映射包含两个列表，分别是 disasterReserves、emergencyShelters、emergencyTeam
    }

    // ----应急物资储备---
    @GetMapping("/reservesList")
    public List<DisasterReliefSupplies> reservesList() {
        return disasterReliefSuppliesService.list();
    }

    //应急物资储备页面--增加功能
    @PostMapping("/addDisasterReserves")
    public boolean addDisasterReserves(@RequestBody DisasterReliefSupplies disasterReliefSupplies) {
        // 打印收到的 geom 字段
        System.out.println("Received geom: " + disasterReliefSupplies.getGeom());
        // 经度验证
        Double longitude = disasterReliefSupplies.getLongitude();
        if (longitude == null) {
            throw new IllegalArgumentException("经度不能为空");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("经度应在 -180 到 180 之间");
        }

        // 纬度验证
        Double latitude = disasterReliefSupplies.getLatitude();
        if (latitude == null) {
            throw new IllegalArgumentException("纬度不能为空");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("纬度应在 -90 到 90 之间");
        }

        return disasterReliefSuppliesService.save(disasterReliefSupplies);
    }

    //应急物资储备页面--删除功能
    @DeleteMapping("/deleteDisasterReserves/{uuid}")
    public boolean deleteDisasterReserves(@PathVariable String uuid) {
        return disasterReliefSuppliesService.removeById(uuid);
    }

    //应急物资储备页面--修改功能
    @PutMapping("/updateDisasterReserves")
    public boolean updateDisasterReserves(@RequestBody DisasterReliefSupplies disasterReliefSupplies) {
        return disasterReliefSuppliesService.updateById(disasterReliefSupplies);
    }

    //    应急物资储备页面--搜索功能 介绍：<县(区)、地址、联系人、联系电话>搜索具体的内容；<合计总件套数、救灾帐篷(顶)、棉被(床)>等等数量字段，仅搜索字段名且字段数据数量>0
    @PostMapping("/searchDisasterReserves")
    public List<DisasterReliefSupplies> searchDisasterReserves(@RequestParam("inputData") String inputData) {
        QueryWrapper<DisasterReliefSupplies> queryWrapper = new QueryWrapper<>();

        // 1. 文本字段的模糊搜索
        queryWrapper.or(wrapper -> wrapper
                .like("county", inputData)
                .or().like("address", inputData)
                .or().like("contact_person", inputData)
                .or().like("contact_phone", inputData)
                // 使用 PostgreSQL 的 TO_CHAR 函数将日期字段转换为字符串
                .or().apply("TO_CHAR(insert_time, 'YYYY-MM-DD') LIKE {0}", "%" + inputData + "%")
        );

        // 查询文本字段匹配的数据
        List<DisasterReliefSupplies> resultList = disasterReliefSuppliesService.list(queryWrapper);

        // 2. 如果文本字段查询未匹配到任何结果，进行数量字段的匹配
        if (resultList.isEmpty()) {
            // 数量字段中文名称与数据库字段名的映射关系
            Map<String, String> fieldMapping = new HashMap<>();
            fieldMapping.put("合计总件套数", "total_items_count");
            fieldMapping.put("救灾帐篷", "tents");
            fieldMapping.put("棉被", "quilts");
            fieldMapping.put("其他被子", "other_blankets");
            fieldMapping.put("棉衣裤", "cotton_clothing");
            fieldMapping.put("棉大衣", "cotton_coats");
            fieldMapping.put("其他衣物", "other_clothing");
            fieldMapping.put("毛毯", "blankets");
            fieldMapping.put("折叠床", "folding_beds");
            fieldMapping.put("高低床", "bunk_beds");
            fieldMapping.put("彩条布", "tarpaulins");
            fieldMapping.put("防潮垫", "moisture_proof_pads");
            fieldMapping.put("发电机", "generators");
            fieldMapping.put("照明灯具", "lighting_fixtures");
            fieldMapping.put("照明灯组", "lighting_sets");
            fieldMapping.put("手电筒", "flashlights");
            fieldMapping.put("雨衣", "raincoats");
            fieldMapping.put("雨靴", "rain_boots");
            fieldMapping.put("其他装备数量", "other_equipment");

            // 检查输入数据是否匹配数量字段
            String fieldName = fieldMapping.get(inputData);
            if (fieldName != null) {
                // 创建新的查询条件，仅查询数量字段大于等于1的数据
                queryWrapper = new QueryWrapper<>();
                queryWrapper.ge(fieldName, 1);
                resultList = disasterReliefSuppliesService.list(queryWrapper);
                System.out.println("resultList"+resultList);
            } else {
                // 如果没有找到匹配的数量字段，则清空结果列表
                resultList = new ArrayList<>();
            }
        }
        return resultList;
    }


    // ----救援队伍页面----
    @GetMapping("/rescueTeamList")
    public List<RescueTeamsInfo> rescueTeamList() {
        return rescueTeamsInfoService.list();
    }

    //救援队伍页面--增加功能
    @PostMapping("/addEmergencyTeam")
    public boolean addEmergencyTeam(@RequestBody RescueTeamsInfo rescueTeamInfo) {
        return rescueTeamsInfoService.save(rescueTeamInfo);
    }

    //救援队伍页面--删除功能
    @DeleteMapping("/deleteEmergencyTeam/{uuid}")
    public boolean deleteEmergencyTeam(@PathVariable String uuid) {
        return rescueTeamsInfoService.removeById(uuid);
    }

    //救援队伍页面--修改功能
    @PutMapping("/updateEmergencyTeam")
    public boolean updateEmergencyTeam(@RequestBody RescueTeamsInfo rescueTeamInfo) {
        System.out.println("救援队伍："+rescueTeamInfo);
        return rescueTeamsInfoService.updateById(rescueTeamInfo);
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
                .or().like("establishment_date", inputData)
                // 将interval字段转换为字符串进行匹配
                .or().apply("preparation_time::text LIKE {0}", "%" + inputData + "%")
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

    //    避难场所页面--增加功能
    @PostMapping("/addEmergencyShelters")
    public boolean addEmergencyShelters(@RequestBody EmergencyShelters emergencyShelters) {
        return emergencySheltersService.save(emergencyShelters);
    }


    //避难场所页面--删除功能
    @DeleteMapping("/deleteEmergencyShelters/{uuid}")
    public boolean deleteEmergencyShelters(@PathVariable String uuid) {
        return emergencySheltersService.removeById(uuid);
    }

    //避难场所页面--修改功能
    @PutMapping("/updateEmergencyShelters")
    public boolean updateEmergencyShelters(@RequestBody EmergencyShelters emergencyShelters) {
        System.out.println("修改表单："+emergencyShelters);
        return emergencySheltersService.updateById(emergencyShelters);
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
                .or().like("secret_level", inputData)
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

    // 行政区划匹配
    @PostMapping("/marchByRegion")
    public Map<String, List<?>> marchByRegion(@RequestBody Map<String,Object> regionCode) {
        Integer code = (Integer) regionCode.get("regionCode");

        QueryWrapper<YaanJson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adcode", code);
        List<YaanJson> list = yaanJsonService.list(queryWrapper);
        String geom = null;

        if (!list.isEmpty()) {
            YaanJson rescueTeamsInfo = list.get(0);
            geom = String.valueOf(rescueTeamsInfo.getGeom());
        }

        // 抢险救援装备
        QueryWrapper<EmergencyRescueEquipment> equipmentQuery = new QueryWrapper<>();
        equipmentQuery.isNotNull("geom");
        List<EmergencyRescueEquipment> emergencyRescueEquipmentList = emergencyRescueEquipmentService.list(equipmentQuery);
        // 救援力量
        QueryWrapper<RescueTeamsInfo> teamsQuery = new QueryWrapper<>();
        teamsQuery.isNotNull("geom");
        List<RescueTeamsInfo> rescueTeamsInfoList = rescueTeamsInfoService.list(teamsQuery);
        // 救灾物资储备
        QueryWrapper<DisasterReliefSupplies> suppliesQuery = new QueryWrapper<>();
        suppliesQuery.isNotNull("geom");
        List<DisasterReliefSupplies> disasterReliefSuppliesList = disasterReliefSuppliesService.list(suppliesQuery);

        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);

        List<EmergencyRescueEquipment> insideEmergencyRescueEquipment = new ArrayList<>();
        List<RescueTeamsInfo> insideRescueTeamsInfo = new ArrayList<>();
        List<DisasterReliefSupplies> insideDisasterReliefSupplies = new ArrayList<>();

        try {
            MultiPolygon multiPolygon = null;
            try {
                if (geom.startsWith("SRID=4326;")) {
                    geom = geom.substring("SRID=4326;".length());
                }
                multiPolygon = (MultiPolygon) reader.read(geom);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 抢险救援装备
            for (EmergencyRescueEquipment equipment : emergencyRescueEquipmentList) {
                String equipmentGeom = String.valueOf(equipment.getGeom());
                Point pointToCheck = (Point) reader.read(equipmentGeom);
                if (multiPolygon.contains(pointToCheck)) {
                    insideEmergencyRescueEquipment.add(equipment);
                }
            }

            // 救援力量
            for (RescueTeamsInfo teamsInfo : rescueTeamsInfoList) {
                String teamsInfoGeom = String.valueOf(teamsInfo.getGeom());
                Point pointToCheck = (Point) reader.read(teamsInfoGeom);
                if (multiPolygon.contains(pointToCheck)) {
                    insideRescueTeamsInfo.add(teamsInfo);
                }
            }

            // 救灾物资储备
            for (DisasterReliefSupplies supplies : disasterReliefSuppliesList) {
                String suppliesGeom = String.valueOf(supplies.getGeom());
                Point pointToCheck = (Point) reader.read(suppliesGeom);
                if (multiPolygon.contains(pointToCheck)) {
                    insideDisasterReliefSupplies.add(supplies);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, List<?>> emergencyData = new HashMap<>();
        emergencyData.put("insideEmergencyRescueEquipment", insideEmergencyRescueEquipment);
        emergencyData.put("insideRescueTeamsInfo", insideRescueTeamsInfo);
        emergencyData.put("insideDisasterReliefSupplies", insideDisasterReliefSupplies);

        return emergencyData;
    }



}
