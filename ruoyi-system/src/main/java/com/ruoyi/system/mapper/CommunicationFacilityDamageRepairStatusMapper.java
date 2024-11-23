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
            "FROM communication_facility_damage_repair_status yas " +
            "JOIN LATERAL (" +
            "    SELECT earthquake_zone_name, " +
            "           reporting_deadline, " +
            "           system_insertion_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY earthquake_zone_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (reporting_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insertion_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM communication_facility_damage_repair_status " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND earthquake_zone_name = yas.earthquake_zone_name " +
            ") sub ON yas.earthquake_zone_name = sub.earthquake_zone_name " +
            "AND yas.reporting_deadline = sub.reporting_deadline " +
            "AND yas.system_insertion_time = sub.system_insertion_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.earthquake_zone_name")
    List<CommunicationFacilityDamageRepairStatus> fromCommunicationFacilityDamageRepairStatus(@Param("eqid") String eqid, @Param("time") LocalDateTime time);
}