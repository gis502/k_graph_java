package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.EarthquakeList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Mapper
public interface EarthquakeListMapper extends BaseMapper<EarthquakeList> {
    @Select("SELECT * FROM earthquake_list WHERE ST_DWithin(geom, #{point}, #{distance})")
    List<EarthquakeList> selectWithinDistance(@Param("point") Geometry point, @Param("distance") double distance);

    @Select("SELECT * FROM earthquake_list WHERE eqid = CAST(#{eqId} AS UUID)")
    List<EarthquakeList> findEarthquakeIdByTimeAndPosition(@Param("eqId") String eqId);

}
