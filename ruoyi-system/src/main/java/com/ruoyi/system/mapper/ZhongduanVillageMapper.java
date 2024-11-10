package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.ZhongduanVillage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ZhongduanVillageMapper {

    @Select("SELECT " +
            "(SELECT COUNT(DISTINCT current_interrupted_villages_count) " +
            " FROM communication_facility_damage_repair_status " +
            " WHERE earthquake_id = #{eqid}) AS CurrentInterruptedVillages, " +
            "(SELECT COUNT(DISTINCT currently_blacked_out_villages) " +
            " FROM power_supply_information " +
            " WHERE earthquake_id = #{eqid}) AS CurrentlyBlackedOutVillages, " +
            "(SELECT COUNT(DISTINCT villages_with_road_closures) " +
            " FROM road_damage " +
            " WHERE earthquake_id = #{eqid}) AS RoadBlockVillage, " +
            "(SELECT MAX(reporting_deadline) " +
            " FROM (SELECT reporting_deadline " +
            "       FROM communication_facility_damage_repair_status " +
            "       WHERE earthquake_id = #{eqid} " +
            "       UNION ALL " +
            "       SELECT reporting_deadline " +
            "       FROM power_supply_information " +
            "       WHERE earthquake_id = #{eqid} " +
            "       UNION ALL " +
            "       SELECT reporting_deadline " +
            "       FROM road_damage " +
            "       WHERE earthquake_id = #{eqid}) AS all_reportings) AS insertTime")
    List<ZhongduanVillage> selectVillageByEqid(String eqid);
}
