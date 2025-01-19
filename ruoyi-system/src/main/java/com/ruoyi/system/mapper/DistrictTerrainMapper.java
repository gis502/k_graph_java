package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import com.ruoyi.system.domain.entity.DistrictTerrain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@PlotInfoMapper
public interface DistrictTerrainMapper extends BaseMapper<DistrictTerrain> {
    @Select("SELECT * FROM district_terrain WHERE county_district = #{address}")
   DistrictTerrain getDistrictTerrain(@Param("address") String address);
}
