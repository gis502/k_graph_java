package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.domain.entity.EconomicLoss;

import java.util.List;
import java.util.Map;

public interface EconomicLossService extends IService<EconomicLoss> {
    List<EconomicLoss> selectEconomicLossByEqid(String eqid);

    void saveEconomicLoss(List<Map<String, Object>> economicLossList);
}
