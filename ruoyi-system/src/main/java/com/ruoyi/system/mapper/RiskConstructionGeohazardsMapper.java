package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.RiskConstructionGeohazards;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
