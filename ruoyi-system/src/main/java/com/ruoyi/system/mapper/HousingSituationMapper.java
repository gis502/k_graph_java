package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.HousingSituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HousingSituationMapper extends BaseMapper<HousingSituation> {
    @Select("""
    SELECT psi.*
    FROM (
        SELECT psi.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY psi.affected_area_name
                   ORDER BY psi.submission_deadline DESC, psi.system_insert_time DESC
               ) AS rn
        FROM housing_situation psi
        WHERE psi.earthquake_identifier = #{eqid}
    ) AS psi
    WHERE psi.rn = 1
""")
    List<HousingSituation> selectHousingSituationById(String eqid);
}