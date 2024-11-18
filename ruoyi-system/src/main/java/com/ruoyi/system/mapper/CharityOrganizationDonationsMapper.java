package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CharityOrganizationDonations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CharityOrganizationDonationsMapper extends BaseMapper<CharityOrganizationDonations> {
    @Select("""
        WITH RankedRecords AS (
            SELECT
                *,
                ROW_NUMBER() OVER (
                    PARTITION BY earthquake_area_name
                    ORDER BY submission_deadline DESC, system_insert_time DESC
                ) AS rn
            FROM
                charity_organization_donations
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
    List<CharityOrganizationDonations> CharityOrganizationDonationsEqId(String eqid);

    @Select("SELECT yas.* " +
            "FROM charity_organization_donations yas " +
            "JOIN LATERAL (" +
            "    SELECT affected_area, " +
            "           submission_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY affected_area " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (submission_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM charity_organization_donations " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND affected_area = yas.affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND yas.submission_deadline = sub.submission_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.affected_area")
    List<CharityOrganizationDonations> fromCharityOrganizationDonations(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
