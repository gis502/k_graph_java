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
        typeMapping.put("待命队伍", "RescueTeam");
        typeMapping.put("已出发队伍", "RescueTeam");
        typeMapping.put("正在参与队伍", "RescueTeam");

        //救援行动类-营救搜索区域类表
        typeMapping.put("未搜索区域", "RescueActionSearchArea");
        typeMapping.put("已搜索区域", "RescueActionSearchArea");
        typeMapping.put("未营救区域", "RescueActionSearchArea");
        typeMapping.put("已营救区域", "RescueActionSearchArea");
        typeMapping.put("正在营救区域", "RescueActionSearchArea");

        //救援行动类-失踪人员类表
        typeMapping.put("失踪人员", "RescueActionMissingPersons");

        //救援行动类-伤亡人员类表
        typeMapping.put("伤亡人员", "RescueActionCasualties");
        typeMapping.put("重伤人员", "RescueActionCasualties");
        typeMapping.put("轻伤人员", "RescueActionCasualties");

        //次生/衍生灾害处置信息-滑坡、崩塌表
        typeMapping.put("滑坡", "LandslideCollapse");
        typeMapping.put("崩塌", "LandslideCollapse");

        //房屋信息-建筑物破坏类表
        typeMapping.put("基本完好建筑物", "buildingDamageType");
        typeMapping.put("轻微破坏建筑物", "buildingDamageType");
        typeMapping.put("中等破坏建筑物", "buildingDamageType");
        typeMapping.put("严重破坏建筑物", "buildingDamageType");
        typeMapping.put("毁坏或倒塌建筑物", "buildingDamageType");

        typeMapping.forEach((key, value) ->
                System.out.println("PlotType to MapperType mapping: " + key + " -> " + value)
        );
        // 添加其他类型的映射
        return typeMapping;
    }

}
