package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.entity.ZhongduanVillage;
import com.ruoyi.system.mapper.ZhongduanVillageMapper;
import com.ruoyi.system.service.ZhongduanVillageService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

@Service
public class ZhongduanVillageServiceImpl implements ZhongduanVillageService {

    @Resource
    private ZhongduanVillageMapper zhongduanVillageMapper;

    @Override
    public List<ZhongduanVillage> getVillageByEqid(String eqid) {
        return zhongduanVillageMapper.selectVillageByEqid(eqid);
    }
}
