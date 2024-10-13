package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.RescueActionCasualties;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RescueActionCasualtiesService extends IService<RescueActionCasualties>{


    List<RescueActionCasualties> getByEqid(String eqid);
}

