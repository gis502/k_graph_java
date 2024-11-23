package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import com.ruoyi.system.domain.entity.FireDisaster;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@PlotInfoMapper
public interface FireDisasterMapper extends BaseMapper<FireDisaster> {
}
