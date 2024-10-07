package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.mapper.BuildingDamageMapper;
import com.ruoyi.system.service.BuildingDamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingDamageServiceImpl extends ServiceImpl<BuildingDamageMapper, BuildingDamage> implements BuildingDamageService {

    @Autowired
    private BuildingDamageMapper buildingDamageMapper;

    @Override
    public List<BuildingDamage> selectBuildingDamageByEqid(String eqid) {
        return buildingDamageMapper.selectBuildingDamageByEqid(eqid); // 根据 eqid 查询
    }
}
