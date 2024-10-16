package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BuildingDamage;

import java.util.List;
import java.util.Map;

public interface BuildingDamageService extends IService<BuildingDamage> {
    List<BuildingDamage> selectBuildingDamageByEqid(String eqid);

    void saveBuildingDamage(List<Map<String, Object>> buildingDamageList);
}
