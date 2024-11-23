package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.HousingSituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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

    @Select("SELECT yas.* " +
            "FROM housing_situation yas " +
            "JOIN LATERAL (" +
            "    SELECT affected_area_name, " +
            "           submission_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY affected_area_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (submission_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM housing_situation " +
            "    WHERE earthquake_identifier = #{eqid} " +
            "    AND affected_area_name = yas.affected_area_name " +
            ") sub ON yas.affected_area_name = sub.affected_area_name " +
            "AND yas.submission_deadline = sub.submission_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_identifier = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.affected_area_name")
    List<HousingSituation> fromHousingSituation(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}