package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface RoadDamageMapper extends BaseMapper<RoadDamage> {
    @Select("SELECT restored_km, pending_repair_km, affected_area, reporting_deadline, system_insert_time " +
            "FROM road_damage " +
            "WHERE earthquake_id = #{eqid}")
    List<RoadDamage> selectRoadRepairsByEqid(@Param("eqid") String eqid);
}