package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplySituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SupplySituationMapper extends BaseMapper<SupplySituation> {
    @Select("""
    SELECT psi.*
    FROM (
        SELECT psi.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY psi.earthquake_area_name
                   ORDER BY psi.report_deadline DESC, psi.system_insert_time DESC
               ) AS rn
        FROM supply_situation psi
        WHERE psi.earthquake_id = #{eqid}
    ) AS psi
    WHERE psi.rn = 1
""")
    List<SupplySituation> selectSupplySituationById(String eqid);

    @Select("SELECT yas.* " +
            "FROM supply_situation yas " +
            "JOIN LATERAL (" +
            "    SELECT earthquake_area_name, " +
            "           report_deadline, " +
            "           system_insert_time, " +
            "           ROW_NUMBER() OVER (" +
            "               PARTITION BY earthquake_area_name " +
            "               ORDER BY " +
            "                   ABS(EXTRACT(EPOCH FROM (report_deadline - #{time}::timestamp))) ASC, " +
            "                   ABS(EXTRACT(EPOCH FROM (system_insert_time - #{time}::timestamp))) ASC" +
            "           ) AS rn " +
            "    FROM supply_situation " +
            "    WHERE earthquake_id = #{eqid} " +
            "    AND earthquake_area_name = yas.earthquake_area_name " +
            ") sub ON yas.earthquake_area_name = sub.earthquake_area_name " +
            "AND yas.report_deadline = sub.report_deadline " +
            "AND yas.system_insert_time = sub.system_insert_time " +
            "WHERE yas.earthquake_id = #{eqid} " +
            "AND sub.rn = 1 " +
            "ORDER BY yas.earthquake_area_name")
    List<SupplySituation> fromSupplySituation(@Param("eqid") String eqid, @Param("time") LocalDateTime time);

}
