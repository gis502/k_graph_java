package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.entity.CasualtyStats;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CasualtyStatsMapper {

    /**
     * 根据地震 ID 获取统计数据
     * 获取统计数据：newly_deceased, newly_missing, newly_injured
     *
     * @param eqid 地震 ID
     * @return 统计数据列表
     */
    @Select("SELECT " +
            "SUM(newly_deceased) AS total_deaths, " +
            "SUM(newly_missing) AS total_missing, " +
            "SUM(newly_injured) AS total_injuries " +
            "FROM public.casualty_report " +
            "WHERE earthquake_identifier = #{eqid}")
    List<CasualtyStats> getCasualtiesStatsById(@Param("eqid") String eqid);

}
