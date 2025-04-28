package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.AssessmentOutput;

import java.util.List;

public interface IAssessmentOutputService extends IService<AssessmentOutput> {
    String gainMap(String eqId,String eqqueueId);

    String gainOutMap(String eqId, String eqqueueId);
}
