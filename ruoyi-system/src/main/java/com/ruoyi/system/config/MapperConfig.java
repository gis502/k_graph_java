package com.ruoyi.system.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：
 * 作者：邵文博
 * 日期：2024/8/30 15:48
 */
@Configuration
public class MapperConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Map<String, BaseMapper<?>> mapperRegistry(){
        Map<String, BaseMapper<?>> registry = new HashMap<>();

        // 扫描所有带有@PlotMapper注解的BaseMapper实现类并注册到registry中
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(PlotInfoMapper.class);
        for (Object bean : beansWithAnnotation.values()) {
            if (bean instanceof BaseMapper) {
                // 获取实际的接口
                Class<?>[] interfaces = bean.getClass().getInterfaces();
                if (interfaces.length > 0) {
                    System.out.println("Actual Mapper Interface: " + interfaces[0].getName());
                }
                String mapperType = determineMapperType((BaseMapper<?>) bean);
                System.out.println("Registering mapper: " + mapperType + " -> " + bean.getClass().getName());
                registry.put(mapperType, (BaseMapper<?>) bean);
            }
        }
        System.out.println(registry);
        return registry;
    }
    private String determineMapperType(BaseMapper<?> mapper) {
        Class<?> mapperClass = mapper.getClass();
        // 获取实际接口类（即 Mapper 接口）
        Class<?>[] interfaces = mapperClass.getInterfaces();
        if (interfaces.length > 0) {
            String className = interfaces[0].getSimpleName();
            // 假设每个 Mapper 的命名规则为 <类型> + "Mapper"
            return className.replace("Mapper", "").toLowerCase();
        }
        // 如果没有找到接口，则返回空字符串或抛出异常
        return "";
    }

    @Bean
    public Map<String, String> plotTypeToMapperType() {
        Map<String, String> typeMapping = new HashMap<>();
        //救援力量类-队伍状态类表
        typeMapping.put("待命队伍", "rescueTeam");
        typeMapping.put("已出发队伍", "rescueTeam");
        typeMapping.put("正在参与队伍", "rescueTeam");

        //军事箭头
        typeMapping.put("直线箭头", "militaryArrows");
        typeMapping.put("攻击箭头", "militaryArrows");
        typeMapping.put("钳击箭头", "militaryArrows");

        //救援行动类-营救搜索区域类表
        typeMapping.put("未搜索区域", "rescueActionSearchArea");
        typeMapping.put("已搜索区域", "rescueActionSearchArea");
        typeMapping.put("未营救区域", "rescueActionSearchArea");
        typeMapping.put("已营救区域", "rescueActionSearchArea");
        typeMapping.put("正在营救区域", "rescueActionSearchArea");

        //救援行动类-失踪人员类表
        typeMapping.put("失踪人员", "rescueActionMissingPersons");

        //救援行动类-伤亡人员类表
        typeMapping.put("死亡人员", "rescueActionCasualties");
        typeMapping.put("重伤人员", "rescueActionCasualties");
        typeMapping.put("轻伤人员", "rescueActionCasualties");
        typeMapping.put("危重伤人员", "rescueActionCasualties");

        //次生/衍生灾害处置信息-滑坡、崩塌表
        typeMapping.put("滑坡", "landslideCollapse");
        typeMapping.put("崩塌", "landslideCollapse");

        //震后生成-灾害现场动态信息-次生/衍生灾害处置信息-泥石流
        typeMapping.put("泥石流", "debrisFlow");

        //震后生成-灾害现场动态信息-次生/衍生灾害处置信息-地面沉降,地面塌陷
        typeMapping.put("地面塌陷", "groundSettlementSinkhole");

        //震后生成-灾害现场动态信息-次生/衍生灾害处置信息-地裂缝
        typeMapping.put("地裂缝", "fissure");

        //房屋信息-建筑物破坏类表
        typeMapping.put("基本完好建筑物", "buildingDamageType");
        typeMapping.put("轻微破坏建筑物", "buildingDamageType");
        typeMapping.put("中等破坏建筑物", "buildingDamageType");
        typeMapping.put("严重破坏建筑物", "buildingDamageType");
        typeMapping.put("毁坏或倒塌建筑物", "buildingDamageType");

        //交通设施破坏类-限制通行公路、不可通行公路表
        typeMapping.put("限制通行公路", "restrictedUnusableRoads");
        typeMapping.put("不可通行公路", "restrictedUnusableRoads");
        //交通设施破坏类-公路破坏点
        typeMapping.put("公路破坏点", "roadDamagePoints");
        //交通设施破坏类-交通管制点
        typeMapping.put("交通管制点", "trafficControlPoints");
        //交通设施破坏类-不可通行铁路，铁路破坏点
        typeMapping.put("不可通行铁路", "unusableRailwaysDamagePoints");
        typeMapping.put("铁路破坏点", "unusableRailwaysDamagePoints");

        //交通设施破坏类-飞行区等级可用机场，不可用机场表
        typeMapping.put("可用机场", "usableUnusableAirports");
        typeMapping.put("不可用机场", "usableUnusableAirports");
        //交通设施破坏类-限制通行桥梁，不可通行桥梁表
        typeMapping.put("限制通行桥梁", "trafficFacilityDamage");
        typeMapping.put("不可通行桥梁", "trafficFacilityDamage");
        //交通设施破坏类-不可通行隧道表
        typeMapping.put("不可通行隧道", "unpassableTunnel");
        //生命线工程破坏类-不可用供水管网表
        typeMapping.put("不可用供水管网", "unavailableWaterSupplyNetwork");
        //生命线工程破坏类-供水管线破坏点表
        typeMapping.put("供水管线破坏点", "waterSupplyPipelineDamagePoint");
        //生命线工程破坏类-不可用输、配电线路表
        typeMapping.put("不可用输、配电线路", "usableUnusableAirports");
        //
        typeMapping.put("输、配电线路破坏点", "powerLineDamage");
        //生命线工程破坏类-不可用输气管线表
        typeMapping.put("输、不可用输气管线", "unusableGasPipes");
        //交通设施破坏类-
        typeMapping.put("供气管线破坏点", "gasPipelineDamagePoints");
        //水利工程破坏类-堰塞湖表
        typeMapping.put("堰塞湖", "damLake");
        //水利工程破坏类-基本完好大坝,中等破坏大坝,严重破坏大坝表
        typeMapping.put("基本完好大坝", "damCondition");
        typeMapping.put("中等破坏大坝", "damCondition");
        typeMapping.put("严重破坏大坝", "damCondition");
        //水利工程破坏类-基本完好堤防，中等破坏堤防，严重破坏堤防表
        typeMapping.put("基本完好堤防", "leveeDamage");
        typeMapping.put("中等破坏堤防", "leveeDamage");
        typeMapping.put("严重破坏堤防", "leveeDamage");
        //衍生安全生产事故-爆炸表
        typeMapping.put("爆炸", "explosion");
        //衍生安全生产事故-火灾表
        typeMapping.put("火灾", "fireDisaster");
        //衍生安全生产事故-有毒物质泄露表
        typeMapping.put("有毒物质泄露", "toxicSubstanceLeak");
        //衍生安全生产事故-核污染表
        typeMapping.put("核污染", "nuclearPollution");
        //应急避难功能区-室外型避难场所，室内型避难场所表
        typeMapping.put("室外型避难场所", "emergencyShelterFunctionArea");
        typeMapping.put("室内型避难场所", "emergencyShelterFunctionArea");
        //应急避难功能区-常备避险安置点表表
        typeMapping.put("常备避险安置点", "permanentShelter");
        //应急避难功能区-临时避险安置点表
        typeMapping.put("临时避险安置点", "temporaryShelter");
        //应急避难功能区-救灾物资储备库表
        typeMapping.put("救灾物资储备库", "disasterReliefSupplyDepot");

        typeMapping.forEach((key, value) ->
                System.out.println("PlotType to MapperType mapping: " + key + " -> " + value)
        );
        // 添加其他类型的映射
        return typeMapping;
    }

}
