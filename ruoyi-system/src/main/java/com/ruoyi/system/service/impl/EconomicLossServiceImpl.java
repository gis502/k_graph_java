package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.domain.entity.EconomicLoss;
import com.ruoyi.system.mapper.BuildingDamageMapper;
import com.ruoyi.system.mapper.EconomicLossMapper;
import com.ruoyi.system.service.BuildingDamageService;
import com.ruoyi.system.service.EconomicLossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EconomicLossServiceImpl extends ServiceImpl<EconomicLossMapper, EconomicLoss> implements EconomicLossService {

    @Autowired
    private EconomicLossMapper economicLossMapper;

    @Override
    public List<EconomicLoss> selectEconomicLossByEqid(String eqid) {
        return economicLossMapper.selectEconomicLossByEqid(eqid);
    }

    @Override
    public void saveEconomicLoss(List<Map<String, Object>> economicLossList) {

        for (Map<String, Object> damageData : economicLossList) {
            EconomicLoss economicLoss = new EconomicLoss();
            economicLoss.setEqid((String) damageData.get("eqid"));
            economicLoss.setCounty((String) damageData.get("county"));
            economicLoss.setAmount(((Number) damageData.get("amount")).doubleValue());

            economicLossMapper.insert(economicLoss);
        }
    }

}
