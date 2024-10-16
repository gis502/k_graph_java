package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.BuildingDamage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BuildingDamageMapper extends BaseMapper<BuildingDamage> {
    @Select("SELECT * FROM building_damage WHERE eqid = #{eqid}")
    List<BuildingDamage> selectBuildingDamageByEqid(@Param("eqid") String eqid); // 根据 eqid 查询
}

