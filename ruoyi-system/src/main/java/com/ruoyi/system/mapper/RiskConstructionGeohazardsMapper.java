package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RiskConstructionGeohazardsMapper extends BaseMapper<RiskConstructionGeohazards> {

    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY quake_area_name
                    ORDER BY report_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                risk_construction_geohazards
            WHERE
                earthquake_id = #{eqid}
        )
        SELECT
            *
        FROM
            RankedRecords
        WHERE
            rn = 1
    """)
    List<RiskConstructionGeohazards> RiskConstructionGeohazardsByEqId(String eqid);

    @Select("SELECT yas.* " +
            "FROM risk_construction_geohazards yas " +
            "JOIN LATERAL (" +
            "    SELECT quake_area_name, " +
            "           report_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY quake_area_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (report_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM risk_construction_geohazards " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND quake_area_name = yas.quake_area_name " +
            ") sub ON yas.quake_area_name = sub.quake_area_name " +
            "AND yas.report_deadline = sub.report_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.quake_area_name")
    List<RiskConstructionGeohazards> fromRiskConstructionGeohazards(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
