package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import com.ruoyi.system.domain.entity.DistrictTerrain;
import com.ruoyi.system.mapper.DistrictEconomyMapper;
import com.ruoyi.system.mapper.DistrictTerrainMapper;
import com.ruoyi.system.service.DistrictEconomyService;
import com.ruoyi.system.service.DistrictTerrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictTerrainServiceImpl extends ServiceImpl<DistrictTerrainMapper, DistrictTerrain> implements DistrictTerrainService {
    @Autowired
    private DistrictTerrainMapper districtTerrainMapper;

    @Override
    public DistrictTerrain getDistrictTerrain(String address) {
        return districtTerrainMapper.getDistrictTerrain(address);
    }
}
