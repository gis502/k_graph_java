package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.BarrierLakeSituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BarrierLakeSituationMapper extends BaseMapper<BarrierLakeSituation> {

    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY affected_area
                    ORDER BY reporting_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                barrier_lake_situation
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

    List<BarrierLakeSituation> BarrierLakeSituationByEqId(String eqid);
}

