package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SituationPlot;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SituationPlotService extends IService<SituationPlot>{

    void addPlot(String plotType, Object details);

    void deletePlot(String plotType, String plotId);

    void updatePlotDetails(String startTime,String endTime,String plotType, String plotId, Object details);

    Object getPlotInfos(String plotType, String plotId);

    List<Object> getExcelPlotInfo(List<String> plotTypes, List<String> plotIds);

    List<SituationPlot> getPlot(String eqid);

    List<SituationPlot> getSituationPlotsByEqId(String eqid);

    void savePlotDataAndProperties(List<SituationPlot> plotDataList, List<String> updatedPlotProperty);
}
