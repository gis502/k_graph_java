package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SupplySituation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SupplySituationMapper extends BaseMapper<SupplySituation> {

    @Select("SELECT DISTINCT ON (earthquake_area_name) " +
            "earthquake_area_name, " +
            "centralized_water_project_damage, " +
            "report_deadline, " +
            "system_insert_time, " +
            "earthquake_time, " +
            "earthquake_name " +
            "FROM supply_situation " +
            "WHERE earthquake_id = #{eqid} " +  // 使用参数化查询
            "ORDER BY earthquake_area_name, " +
            "system_insert_time DESC")
    List<SupplySituation> selectSupplySituationById(String eqid);
}
