package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.RiskPoints;

import java.util.List;


public interface RiskPointsService extends IService<RiskPoints> {

    List<RiskPoints> riskPointslist(LambdaQueryWrapper<RiskPoints> wrapper, Double epicentreLongitude, Double epicentreLatitude);
}
