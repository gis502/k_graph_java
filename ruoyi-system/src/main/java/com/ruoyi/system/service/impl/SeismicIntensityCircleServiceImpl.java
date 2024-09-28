package com.ruoyi.system.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SeismicIntensityCircle;
import com.ruoyi.system.mapper.SeismicIntensityCircleMapper;
import com.ruoyi.system.service.SeismicIntensityCircleService;
@Service
public class SeismicIntensityCircleServiceImpl extends ServiceImpl<SeismicIntensityCircleMapper, SeismicIntensityCircle> implements SeismicIntensityCircleService{
    @Autowired
    private SeismicIntensityCircleMapper seismicIntensityCircleMapper;

    @Override
    public List<SeismicIntensityCircle> selectCircleByEqid(String eqid) {
        return seismicIntensityCircleMapper.selectCircleByEqid(eqid);
    }

    @Override
    public int addCircle(SeismicIntensityCircle seismicIntensityCircle) {
        return seismicIntensityCircleMapper.addCircle(seismicIntensityCircle);
    }
}
