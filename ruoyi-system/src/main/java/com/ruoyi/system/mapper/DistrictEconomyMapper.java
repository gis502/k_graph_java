package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.annotation.PlotInfoMapper;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DistrictEconomyMapper extends BaseMapper<DistrictEconomy> {

    @Select("SELECT * FROM district_economy WHERE (county_district, year) IN (SELECT county_district, MAX(year) FROM district_economy GROUP BY county_district)ORDER BY year DESC")
    List<DistrictEconomy> getDistrictEconomy();
}
