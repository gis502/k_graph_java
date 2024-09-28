package com.ruoyi.system.service;

import com.ruoyi.system.domain.SeismicIntensityCircle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SeismicIntensityCircleService extends IService<SeismicIntensityCircle>{

    public List<SeismicIntensityCircle> selectCircleByEqid(String eqid);
    public int addCircle(SeismicIntensityCircle seismicIntensityCircle);
}
