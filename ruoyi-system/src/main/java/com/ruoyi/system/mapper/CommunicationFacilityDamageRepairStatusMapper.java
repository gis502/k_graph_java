package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunicationFacilityDamageRepairStatusMapper extends BaseMapper<CommunicationFacilityDamageRepairStatus> {

    @Select("""
    SELECT psi.*
    FROM (
        SELECT psi.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY psi.earthquake_zone_name
                   ORDER BY psi.reporting_deadline DESC, psi.system_insertion_time DESC
               ) AS rn
        FROM communication_facility_damage_repair_status psi
        WHERE psi.earthquake_id = #{eqid}
    ) AS psi
    WHERE psi.rn = 1
""")
    List<CommunicationFacilityDamageRepairStatus> facility(@Param("eqid") String eqid);
}