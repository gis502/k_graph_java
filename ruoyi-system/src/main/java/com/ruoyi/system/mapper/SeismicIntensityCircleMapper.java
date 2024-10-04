package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SeismicIntensityCircle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SeismicIntensityCircleMapper extends BaseMapper<SeismicIntensityCircle> {

    List<SeismicIntensityCircle> selectCircleByEqid(String eqid);
    int addCircle(SeismicIntensityCircle seismicIntensityCircle);

    String selectBigOutCircleByEqid(String eqid);

    int selectCenterintensityByEqid(String eqid);
}