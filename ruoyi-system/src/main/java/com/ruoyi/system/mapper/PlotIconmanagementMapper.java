package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PlotIconmanagement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlotIconmanagementMapper extends BaseMapper<PlotIconmanagement> {

    List<PlotIconmanagement> findIconsBySheetNames(@Param("sheetNames") List<String> sheetNames);
    List<PlotIconmanagement> searchPlotIcon(@Param("menuName") String menuName);
}