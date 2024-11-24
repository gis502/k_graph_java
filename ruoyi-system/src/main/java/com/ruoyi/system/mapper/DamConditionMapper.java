package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import com.ruoyi.system.domain.entity.DamCondition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@PlotInfoMapper
public interface DamConditionMapper extends BaseMapper<DamCondition> {
}
