package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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

    @Select("SELECT yas.* " +
            "FROM public.communication_facility_damage_repair_status yas " +
            "JOIN ( " +
            "    SELECT affected_area, MIN(ABS(EXTRACT(EPOCH FROM (earthquake_time - #{time})))) AS min_time_diff " +
            "    FROM public.communication_facility_damage_repair_status " +
            "    WHERE earthquake_id = #{eqid} " +
            "    GROUP BY affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND ABS(EXTRACT(EPOCH FROM (yas.earthquake_time - #{time}))) = sub.min_time_diff " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "ORDER BY yas.affected_area")
    List<CommunicationFacilityDamageRepairStatus> fromCommunicationFacilityDamageRepairStatus(@Param("eqid") String eqid, @Param("time") LocalDateTime time);
}