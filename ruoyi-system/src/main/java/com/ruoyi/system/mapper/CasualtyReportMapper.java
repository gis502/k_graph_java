package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CasualtyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CasualtyReportMapper extends BaseMapper<CasualtyReport> {

    @Select("SELECT " +
            "SUM(newly_deceased) AS total_deaths, " +
            "SUM(newly_missing) AS total_missing, " +
            "SUM(newly_injured) AS total_injuries, " +
            "MAX(submission_deadline) AS submission_deadline " +
            "FROM public.casualty_report " +
            "WHERE earthquake_identifier = #{eqid}")
    CasualtyReport getCasualtiesStatsById(@Param("eqid") String eqid);

//    @Select("SELECT " +
//            "SUM(total_deceased) AS total_deceased, " +
//            "SUM(total_missing) AS total_missing, " +
//            "SUM(total_injured) AS total_injured, " +
//            "MAX(submission_deadline) AS submission_deadline " +
//            "FROM public.casualty_report " +
//            "WHERE earthquake_identifier = #{eqid}")
//    CasualtyReport getTotal(@Param("eqid") String eqid);

    @Select("SELECT cr.affected_area_name, " +
            "SUM(cr.total_deceased) AS total_deceased, " +
            "SUM(cr.total_missing) AS total_missing, " +
            "SUM(cr.total_injured) AS total_injured, " +
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
    List<CasualtyReport> getTotal(@Param("eqid") String eqid);
}


