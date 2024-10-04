package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SeismicIntensityCircle;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SeismicIntensityCircleService extends IService<SeismicIntensityCircle>{

    public List<SeismicIntensityCircle> selectCircleByEqid(String eqid);
    public int addCircle(SeismicIntensityCircle seismicIntensityCircle);
    public String selectBigOutCircleByEqid(String eqid);
    public Integer selectCenterintensityByEqid(String eqid);

}
