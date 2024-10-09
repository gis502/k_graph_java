package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CasualtyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @Select("SELECT " +
            "SUM(total_deceased) AS total_deceased, " +
            "SUM(total_missing) AS total_missing, " +
            "SUM(total_injured) AS total_injured, " +
            "MAX(submission_deadline) AS submission_deadline " +
            "FROM public.casualty_report " +
            "WHERE earthquake_identifier = #{eqid}")
    CasualtyReport getTotal(@Param("eqid") String eqid);
}


