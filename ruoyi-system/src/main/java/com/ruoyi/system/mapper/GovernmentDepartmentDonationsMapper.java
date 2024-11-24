package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.GovernmentDepartmentDonations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GovernmentDepartmentDonationsMapper extends BaseMapper<GovernmentDepartmentDonations> {
    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY earthquake_area_name
                    ORDER BY submission_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                government_department_donations
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
    List<GovernmentDepartmentDonations> GovernmentDepartmentDonationsEqId(String eqid);

    @Select("SELECT yas.* " +
            "FROM government_department_donations yas " +
            "JOIN LATERAL (" +
            "    SELECT earthquake_area_name, " +
            "           submission_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY earthquake_area_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (submission_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM government_department_donations " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND earthquake_area_name = yas.earthquake_area_name " +
            ") sub ON yas.earthquake_area_name = sub.earthquake_area_name " +
            "AND yas.submission_deadline = sub.submission_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.earthquake_area_name")
    List<GovernmentDepartmentDonations> fromGovernmentDepartmentDonations(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
