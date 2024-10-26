package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.CommunicationFacilityDamageRepairStatus;
import com.ruoyi.system.domain.entity.RoadDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunicationFacilityDamageRepairStatusMapper extends BaseMapper<CommunicationFacilityDamageRepairStatus> {

    @Select("SELECT * FROM communication_facility_damage_repair_status WHERE earthquake_id = #{eqid} " +
            "AND system_insertion_time = (SELECT MAX(system_insertion_time) " +
            "FROM communication_facility_damage_repair_status AS r WHERE r.earthquake_zone_name = communication_facility_damage_repair_status.earthquake_zone_name)")
    List<CommunicationFacilityDamageRepairStatus> facility(@Param("eqid") String eqid);
}