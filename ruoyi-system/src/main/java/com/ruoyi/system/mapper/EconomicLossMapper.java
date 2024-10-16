package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.BuildingDamage;
import com.ruoyi.system.domain.entity.EconomicLoss;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EconomicLossMapper extends BaseMapper<EconomicLoss> {
    @Select("SELECT * FROM economic_loss WHERE eqid = #{eqid}")
    List<EconomicLoss> selectEconomicLossByEqid(@Param("eqid") String eqid); // 根据 eqid 查询
}

