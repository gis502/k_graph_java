package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplySituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SupplySituationMapper extends BaseMapper<SupplySituation> {
    @Select("WITH RankedRecords AS ( " +
            "    SELECT s.*, " +
            "           ROW_NUMBER() OVER (PARTITION BY earthquake_area_name ORDER BY report_deadline DESC) AS rn " +
            "    FROM public.supply_situation s " +
            "    WHERE s.earthquake_id = #{eqid} " +
            ") " +
            "SELECT * " +
            "FROM RankedRecords " +
            "WHERE rn = 1 " +
            "ORDER BY earthquake_area_name")



    List<SupplySituation> selectSupplySituationById(String eqid);
}
