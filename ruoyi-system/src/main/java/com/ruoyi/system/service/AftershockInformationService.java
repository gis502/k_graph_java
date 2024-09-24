package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.AftershockInformation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface AftershockInformationService extends IService<AftershockInformation>{

    /**
     * 最新余震情况
     * @param eqid
     * @return
     */
    Map<String, Integer> getLatestAftershockMagnitude(String eqid);
}
