package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.EarthquakeList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EarthquakeListMapper extends BaseMapper<EarthquakeList> {
  List<EarthquakeList> selectAllEq();

}
