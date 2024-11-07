package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplySituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
}
