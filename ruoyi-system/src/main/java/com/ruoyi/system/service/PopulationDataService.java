package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.entity.PopulationData;

import java.util.List;

public interface PopulationDataService extends IService<PopulationData> {
    List<PopulationData> listall();
}
