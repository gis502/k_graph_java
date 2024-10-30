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
        FROM power_supply_information psi
        JOIN (
            SELECT affected_area, MAX(system_insert_time) AS latest_time
            FROM power_supply_information
            WHERE earthquake_id = #{eqid}
            GROUP BY affected_area
        ) AS latest ON psi.affected_area = latest.affected_area
        AND psi.system_insert_time = latest.latest_time
        WHERE psi.earthquake_id = #{eqid}
    """)
    List<PowerSupplyInformation> getPowerSupply(@Param("eqid") String eqid);
}
