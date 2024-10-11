package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.domain.entity.EconomicLoss;

import java.util.List;

public interface EconomicLossService extends IService<EconomicLoss> {
    List<EconomicLoss> selectEconomicLossByEqid(String eqid);
}
