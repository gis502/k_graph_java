package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplyWater;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupplyWaterMapper extends BaseMapper<SupplyWater> {
    @Select("""
    SELECT psi.*
    FROM (
        SELECT psi.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY psi.earthquake_area_name 
                   ORDER BY psi.report_deadline DESC, psi.system_insert_time DESC
               ) AS rn
        FROM supply_water psi
        WHERE psi.earthquake_id = #{eqid}
    ) AS psi
    WHERE psi.rn = 1
""")
    List<SupplyWater> selectSupplyWaterById(String eqid);
}
