package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.HousingSituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HousingSituationMapper extends BaseMapper<HousingSituation> {
    @Select("WITH RankedRecords AS ( " +
            "    SELECT h.*, " +
            "           ROW_NUMBER() OVER (PARTITION BY affected_area_name ORDER BY system_insert_time DESC) AS rn " +
            "    FROM public.housing_situation h " +
            "    WHERE h.earthquake_identifier = #{eqid} " +
            ") " +
            "SELECT * " +
            "FROM RankedRecords " +
            "WHERE rn = 1 " +
            "ORDER BY affected_area_name")
    List<HousingSituation> selectHousingSituationById(String eqid);
}