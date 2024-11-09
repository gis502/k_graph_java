package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SecondaryDisasterInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SecondaryDisasterInfoMapper extends BaseMapper<SecondaryDisasterInfo> {

    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY affected_area
                    ORDER BY reporting_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                secondary_disaster_info
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
    List<SecondaryDisasterInfo> SecondaryDisasterInfoByEqId(String eqid);
}
