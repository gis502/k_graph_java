package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.ZhongduanVillage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ZhongduanVillageMapper {
    @Select("""
    WITH road_damage AS (
        SELECT DISTINCT villages_with_road_closures, system_insert_time
        FROM road_damage
        WHERE earthquake_id = #{eqid}
    ),
    power_supply AS (
        SELECT DISTINCT currently_blacked_out_villages, system_insert_time
        FROM power_supply_information
        WHERE earthquake_id = #{eqid}
    ),
    communication_facility AS (
        SELECT DISTINCT current_interrupted_villages_count, system_insertion_time
        FROM communication_facility_damage_repair_status
        WHERE earthquake_id = #{eqid}
    )
    SELECT
        COUNT(DISTINCT rd.villages_with_road_closures) AS RoadBlockVillage,
        COUNT(DISTINCT ps.currently_blacked_out_villages) AS CurrentlyBlackedOutVillages,
        COUNT(DISTINCT cf.current_interrupted_villages_count) AS CurrentInterruptedVillages,
        GREATEST(
            COALESCE(MAX(rd.system_insert_time), NULL),
            COALESCE(MAX(ps.system_insert_time), NULL),
            COALESCE(MAX(cf.system_insertion_time), NULL)
        ) AS insertTime
    FROM road_damage rd, power_supply ps, communication_facility cf
""")

    List<ZhongduanVillage> selectVillageByEqid(String eqid);
}
