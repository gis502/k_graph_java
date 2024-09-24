package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.AftershockInformation;
import com.ruoyi.system.mapper.AftershockInformationMapper;
import com.ruoyi.system.service.AftershockInformationService;
@Service
public class AftershockInformationServiceImpl extends ServiceImpl<AftershockInformationMapper, AftershockInformation> implements AftershockInformationService{
    @Autowired
    private AftershockInformationMapper aftershockInformationMapper;

    /**
     * 获取最新余震数据
     * @param eqid
     * @return
     */
    @Override
    public Map<String, Integer> getLatestAftershockMagnitude(String eqid) {
        Map<String, Integer> aftershockData = aftershockInformationMapper.getLatestAftershockData(eqid);
        // 检查数据是否为 null 或空
        if (aftershockData == null || aftershockData.isEmpty()) {
            // 返回默认值
            aftershockData = new HashMap<>();
            aftershockData.put("magnitude_3_0_to_3_9", 0);
            aftershockData.put("magnitude_4_0_to_4_9", 0);
            aftershockData.put("magnitude_5_0_to_5_9", 0);
        }
        System.out.println("------------------------------------------");
        System.out.println(aftershockData);
        return aftershockData;
    }
    }

