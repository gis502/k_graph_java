package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.EarthquakeList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EarthquakeListMapper extends BaseMapper<EarthquakeList> {
  List<EarthquakeList> selectAllEq();

  //导入表实用
  @Select("SELECT * FROM earthquake_list WHERE eqid = CAST(#{eqId} AS UUID)")
  List<EarthquakeList> findEarthquakeIdByTimeAndPosition(@Param("eqId") String eqId);


}
