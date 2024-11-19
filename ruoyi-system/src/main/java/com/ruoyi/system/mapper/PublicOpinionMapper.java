package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PublicOpinion;
import com.ruoyi.system.domain.entity.RedCrossDonations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PublicOpinionMapper extends BaseMapper<PublicOpinion> {
    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY earthquake_zone_name
                    ORDER BY submission_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                public_opinion
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
    List<PublicOpinion> getpublicopinion(String eqid);

    @Select("SELECT yas.* " +
            "FROM public_opinion yas " +
            "JOIN LATERAL (" +
            "    SELECT earthquake_zone_name, " +
            "           submission_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY earthquake_zone_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (submission_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM public_opinion " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND earthquake_zone_name = yas.earthquake_zone_name " +
            ") sub ON yas.earthquake_zone_name = sub.earthquake_zone_name " +
            "AND yas.submission_deadline = sub.submission_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.earthquake_zone_name")
    List<PublicOpinion> fromPublicOpinion(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
