package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PowerSupplyInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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

    @Select("SELECT yas.* " +
            "FROM public.power_supply_information yas " +
            "JOIN ( " +
            "    SELECT affected_area, MIN(ABS(EXTRACT(EPOCH FROM (earthquake_time - #{time})))) AS min_time_diff " +
            "    FROM public.power_supply_information " +
            "    WHERE earthquake_id = #{eqid} " +
            "    GROUP BY affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND ABS(EXTRACT(EPOCH FROM (yas.earthquake_time - #{time}))) = sub.min_time_diff " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "ORDER BY yas.affected_area")
    List<PowerSupplyInformation> fromPowerSupplyInformation(@Param("eqid") String eqid, @Param("time") LocalDateTime time);
}