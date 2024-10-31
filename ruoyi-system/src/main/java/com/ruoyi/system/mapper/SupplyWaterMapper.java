package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplyWater;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupplyWaterMapper extends BaseMapper<SupplyWater> {
    @Select("SELECT earthquake_area_name, " +
            "MAX(water_supply_points) AS water_supply_points, " +
            "MAX(report_deadline) AS report_deadline, " +
            "MAX(system_insert_time) AS system_insert_time, " +
            "MAX(earthquake_time) AS earthquake_time, " +
            "MAX(earthquake_name) AS earthquake_name " +
            "FROM public.supply_water " +
            "WHERE earthquake_id = #{eqid} " +
            "GROUP BY earthquake_area_name " +
            "ORDER BY earthquake_area_name")
    List<SupplyWater> selectSupplyWaterById(String eqid);
}
