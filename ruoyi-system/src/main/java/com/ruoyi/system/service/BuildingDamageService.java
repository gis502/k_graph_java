package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BuildingDamage;

import java.util.List;

public interface BuildingDamageService extends IService<BuildingDamage> {
    List<BuildingDamage> selectBuildingDamageByEqid(String eqid);
}
