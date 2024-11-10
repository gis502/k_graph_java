package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PowerSupplyInformationMapper extends BaseMapper<PowerSupplyInformation> {

    @Select("""
    SELECT psi.*
    FROM (
        SELECT psi.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY psi.affected_area 
                   ORDER BY psi.reporting_deadline DESC, psi.system_insert_time DESC
               ) AS rn
        FROM power_supply_information psi
        WHERE psi.earthquake_id = #{eqid}
    ) AS psi
    WHERE psi.rn = 1
""")
    List<PowerSupplyInformation> getPowerSupply(@Param("eqid") String eqid);

}
