package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.EqEventDTO;
import com.ruoyi.system.domain.dto.EqEventTriggerDTO;
import com.ruoyi.system.domain.entity.AssessmentJuece;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AssessmentJueceService extends IService<AssessmentJuece>{


    void saveAssessmentJuece(EqEventTriggerDTO params, String sourceFile);

    List<AssessmentJuece> eqEventOutputJueCeData(EqEventDTO dto);
}
