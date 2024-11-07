package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SituationPlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SituationPlotMapper extends BaseMapper<SituationPlot> {
    List<SituationPlot> getPlot(@Param("eqid") String eqid);

    void insertSituationPlots(List<SituationPlot> plotDataList);

    List<SituationPlot> getCheckPlot(@Param("eqid") String eqid);
}
