package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.DistrictEconomy;
import com.ruoyi.system.domain.entity.PopulationData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PopulationDataMapper extends BaseMapper<PopulationData> {
    @Select("SELECT * FROM population_data WHERE (area, update_time) IN (SELECT area, MAX(update_time) FROM population_data GROUP BY area) ORDER BY area desc")
    List<PopulationData> listGetByMaxYear();
}
