package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoadDamageMapper extends BaseMapper<RoadDamage> {

    @Select("SELECT * FROM road_damage WHERE earthquake_id = #{eqid} " +
            "AND system_insert_time = (SELECT MAX(system_insert_time) " +
            "FROM road_damage AS r WHERE r.affected_area = road_damage.affected_area)")
    List<RoadDamage> selectRoadRepairsByEqid(@Param("eqid") String eqid);
}
