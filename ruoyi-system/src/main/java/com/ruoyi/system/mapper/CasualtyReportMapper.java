package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CasualtyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface CasualtyReportMapper extends BaseMapper<CasualtyReport> {

    /**
     * 通过地震标识的eqid进行条件查询
     *
     * @param eqid 地震标识id
     * @return 结果
     */
    @Select("SELECT " +
            "SUM(newly_deceased) AS total_deaths, " +
            "SUM(newly_missing) AS total_missing, " +
            "SUM(newly_injured) AS total_injuries, " +
            "MAX(submission_deadline) AS submission_deadline " +
            "FROM public.casualty_report " +
            "WHERE earthquake_identifier = #{eqid}")
    CasualtyReport getCasualtiesStatsById(@Param("eqid") String eqid);

    @Select("SELECT cr.affected_area_name, " +
            "SUM(cr.total_deceased) AS total_deceased, " +
            "SUM(cr.total_missing) AS total_missing, " +
            "SUM(cr.total_injured) AS total_injured, " +
            "MAX(cr.submission_deadline) AS submission_deadline, " +
            "cr.system_insert_time " +
            "FROM public.casualty_report cr " +
            "JOIN ( " +
            "    SELECT affected_area_name, MAX(submission_deadline) AS latest_submission_deadline, " +
            "           MAX(system_insert_time) AS latest_system_insert_time " +
            "    FROM public.casualty_report " +
            "    WHERE earthquake_identifier = #{eqid} " +
            "    GROUP BY affected_area_name " +
            ") sub ON cr.affected_area_name = sub.affected_area_name " +
            "AND cr.submission_deadline = sub.latest_submission_deadline " +
            "AND cr.system_insert_time = sub.latest_system_insert_time " +
            "WHERE cr.earthquake_identifier = #{eqid} " +
            "GROUP BY cr.affected_area_name, cr.system_insert_time")
    List<CasualtyReport> getTotal(@Param("eqid") String eqid);

    @Select("SELECT cr.affected_area_name, " +
            "SUM(cr.total_deceased) AS total_deceased, " +
            "SUM(cr.total_missing) AS total_missing, " +
            "SUM(cr.total_injured) AS total_injured, " +
            "SUM(cr.affected_population) AS affected_population, " + // 新增统计affected_population
            "cr.submission_deadline " +
            "FROM public.casualty_report cr " +
            "JOIN ( " +
            "    SELECT affected_area_name, MAX(submission_deadline) AS latest_deadline " +
            "    FROM public.casualty_report " +
            "    WHERE earthquake_identifier = #{eqid} " +
            "    GROUP BY affected_area_name " +
            ") sub ON cr.affected_area_name = sub.affected_area_name " +
            "AND cr.submission_deadline = sub.latest_deadline " +
            "WHERE cr.earthquake_identifier = #{eqid} " +
            "GROUP BY cr.affected_area_name, cr.submission_deadline")
    List<CasualtyReport> getCasualty(@Param("eqid") String eqid);



    @Select("SELECT yas.* " +
            "FROM casualty_report yas " +
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
            "    FROM casualty_report " +
            "    WHERE earthquake_identifier = #{eqid} " +
            "    AND affected_area = yas.affected_area " +
            ") sub ON yas.affected_area = sub.affected_area " +
            "AND yas.submission_deadline = sub.submission_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_identifier = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.affected_area")
    List<Map<String, Object>> fromCasualty(@Param("eqid") String eqid, @Param("time") LocalDateTime time);


}


