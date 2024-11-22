package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.MaterialDonation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MaterialDonationMapper extends BaseMapper<MaterialDonation> {
    @Select("""
        WITH RankedDonations AS (
            SELECT 
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY earthquake_area_name 
                    ORDER BY report_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM 
                material_donation
            WHERE 
                earthquake_id = #{eqid}
        )
        SELECT 
            *
        FROM 
            RankedDonations
        WHERE 
            rn = 1
    """)
    List<MaterialDonation> MaterialDonationEqId(String eqid);

    @Select("SELECT yas.* " +
            "FROM material_donation yas " +
            "JOIN LATERAL (" +
            "    SELECT affected_area, " +
            "           report_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY affected_area " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (report_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM material_donation " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND affected_area = yas.affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND yas.report_deadline = sub.report_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.affected_area")
    List<MaterialDonation> fromMaterialDonation(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
