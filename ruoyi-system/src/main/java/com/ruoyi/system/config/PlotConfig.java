package com.ruoyi.system.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PlotConfig {
    public Map<String, String> getFieldMapping() {
        Map<String, String> fieldMapping = new HashMap<>();

        // 标绘表
        fieldMapping.put("绘制类型", "drawtype");
        fieldMapping.put("经度", "longitude");
        fieldMapping.put("纬度", "latitude");
        fieldMapping.put("经纬度集合", "geomDetails");
        fieldMapping.put("高程", "elevation");
        fieldMapping.put("角度", "angle");
        fieldMapping.put("开始时间", "startTime");
        fieldMapping.put("结束时间", "endTime");

        // 属性表
        fieldMapping.put("队伍名称", "teamName");
        fieldMapping.put("省", "stationProvince");
        fieldMapping.put("市（州）", "stationCity");
        fieldMapping.put("县（市、区）", "stationCounty");
        fieldMapping.put("人员数量", "personnelCount");
        fieldMapping.put("队伍性质", "teamNature");
        fieldMapping.put("队伍类型", "teamType");
        fieldMapping.put("拟抢险救援区域", "plannedRescueArea");
        fieldMapping.put("联系人员", "contactPerson");
        fieldMapping.put("联系电话", "contactPhone");
        fieldMapping.put("抢险救援区域", "plannedRescueArea");
        fieldMapping.put("缓存区面积 (m²)", "area");
        fieldMapping.put("容纳人数", "capacity");
        fieldMapping.put("基本保障", "basicSupport");
        fieldMapping.put("失联位置", "missingLocation");
        fieldMapping.put("新增失踪人数", "newMissingCount");
        fieldMapping.put("累计失踪人数", "totalMissingCount");
        fieldMapping.put("失联原因", "missingReason");
        fieldMapping.put("搜寻队伍", "searchTeam");
        fieldMapping.put("所在位置", "location");
        fieldMapping.put("人员伤亡状态", "casualtyStatus");
        fieldMapping.put("ABCD评分", "abcdScore");
        fieldMapping.put("新增总数", "newCount");
        fieldMapping.put("累计总数", "totalCount");
        fieldMapping.put("医疗救援队伍", "medicalRescueTeam");
        fieldMapping.put("搜索区域位置", "rescueAreaLocation");
        fieldMapping.put("未搜索区域面积", "rescueAreaSizeKm2");
        fieldMapping.put("所需搜索人员数量", "rescueTeamAndPersonnelCount");
        fieldMapping.put("已搜索区域面积", "rescueAreaSizeKm2");
        fieldMapping.put("参与搜索队伍及人员数量", "rescueTeamAndPersonnelCount");
        fieldMapping.put("营救区域位置", "rescueAreaLocation");
        fieldMapping.put("营救区域面积", "rescueAreaSizeKm2");
        fieldMapping.put("所需营救人员数量", "rescueTeamAndPersonnelCount");
        fieldMapping.put("营救队伍及人员数量", "rescueTeamAndPersonnelCount");
        fieldMapping.put("救援装备类型", "rescueEquipmentType");
        fieldMapping.put("是否需要工程机械", "needEngineeringMachinery");
        fieldMapping.put("起始点", "startingPoint");
        fieldMapping.put("目标点", "targetPoint");
        fieldMapping.put("行动内容", "actionContent");
        fieldMapping.put("河沟坡度", "riverSlope");
        fieldMapping.put("植被覆盖率", "vegetationCoverage");
        fieldMapping.put("流速", "flowSpeed");
        fieldMapping.put("面积 (m²)", "area");
        fieldMapping.put("平均厚度 (m)", "averageThicknessM");
        fieldMapping.put("物质组成", "materialComposition");
        fieldMapping.put("目前稳定状况", "currentStabilityStatus");
        fieldMapping.put("人员伤亡", "casualties");
        fieldMapping.put("威胁对象", "threatenedObjects");
        fieldMapping.put("处置队伍", "disposalTeam");
        fieldMapping.put("处置措施", "disposalMeasures");
        fieldMapping.put("先期处置阶段", "initialDisposalPhase");
        fieldMapping.put("长度 (m)", "lengthM");
        fieldMapping.put("宽度 (m)", "widthM");
        fieldMapping.put("体积 (m³)", "volumeM3");
        fieldMapping.put("沉降面积 (m²)", "affectedArea");
        fieldMapping.put("累计沉降量 (h) (m)", "cumulativeSettlement");
        fieldMapping.put("发展变化", "developmentChange");
        fieldMapping.put("影响范围面积 (m²)", "affectedArea");
        fieldMapping.put("塌陷坑直径 (m)", "cumulativeSettlement");
        fieldMapping.put("使用性质", "usageType");
        fieldMapping.put("结构类型", "structureType");
        fieldMapping.put("破坏形式", "damageForm");
        fieldMapping.put("破坏模式", "damageForm");
        fieldMapping.put("处置阶段", "initialDisposalPhase");
        fieldMapping.put("公路名称", "roadName");
        fieldMapping.put("公路级别", "roadLevel");
        fieldMapping.put("公路等级", "roadLevel");
        fieldMapping.put("管制类型", "controlType");
        fieldMapping.put("管制单位", "controlUnit");
        fieldMapping.put("铁路名称", "railwayName");
        fieldMapping.put("铁路等级", "railwayLevel");
        fieldMapping.put("机场名称", "airportName");
        fieldMapping.put("飞行区等级", "airportLevel");
        fieldMapping.put("桥梁名称", "bridgeName");
        fieldMapping.put("所在公路", "roadName");
        fieldMapping.put("桥梁类型", "bridgeType");
        fieldMapping.put("隧道名称", "tunnelName");
        fieldMapping.put("所在线路", "line");
        fieldMapping.put("隧道类型", "tunnelType");
        fieldMapping.put("供电区域", "supplyArea");
        fieldMapping.put("电压等级", "voltageLevel");
        fieldMapping.put("管理单位", "managementUnit");
        fieldMapping.put("供气区域", "supplyArea");
        fieldMapping.put("管道分类", "pipeType");
        fieldMapping.put("供水区域", "waterSupplyArea");
        fieldMapping.put("供水管网类型", "waterSupplyNetworkType");
        fieldMapping.put("所在水系", "riverSystem");
        fieldMapping.put("顺河长度 (m)", "damScaleLength");
        fieldMapping.put("高度 (m)", "damScaleHeight");
        fieldMapping.put("堰塞体危险性", "estimatedDanger");
        fieldMapping.put("预估库容", "estimatedStorageCapacity");
        fieldMapping.put("淹没和溃决影响区", "area");
        fieldMapping.put("水库（电站）名称", "reservoirName");
        fieldMapping.put("大坝类型", "damType");
        fieldMapping.put("工程规模", "projectScale");
        fieldMapping.put("防洪标准", "floodStandard");
        fieldMapping.put("建筑材料", "constructionMaterial");
        fieldMapping.put("企业（单位）名称", "enterpriseName");
        fieldMapping.put("爆炸类型", "explosionType");
        fieldMapping.put("影响范围 (m²)", "impactArea");
        fieldMapping.put("火灾类型", "fireType");
        fieldMapping.put("火灾等级", "fireLevel");
        fieldMapping.put("有毒物质类型", "toxicSubstanceType");
        fieldMapping.put("安置点名称", "shelterName");
        fieldMapping.put("安置点类型", "shelterType");
        fieldMapping.put("基础保障", "basicFacilities");
        fieldMapping.put("功能区", "functionArea");
        fieldMapping.put("管护单位", "managementUnit");
        fieldMapping.put("储备库名称", "depotName");
        fieldMapping.put("储备库位置", "depotLocation");
        fieldMapping.put("储备库级别", "depotLevel");
        fieldMapping.put("物资类型", "materialType");
        fieldMapping.put("场所名称", "shelterName");
        fieldMapping.put("场所级别", "shelterLevel");
        fieldMapping.put("场所功能", "shelterFunction");
        fieldMapping.put("场所类型", "shelterType");
        fieldMapping.put("避难人员数量", "shelterCapacity");

        // 添加更多字段

        return fieldMapping;
    }
}
