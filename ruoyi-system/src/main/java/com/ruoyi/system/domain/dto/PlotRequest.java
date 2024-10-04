package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.SituationPlot;
import lombok.Data;
import org.postgis.Geometry;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

/**
 * 功能：
 * 作者：邵文博
 * 日期：2024/10/1 1:18
 */
@Data
public class PlotRequest {
    private SituationPlot plot;
    private Map<String, Object> plotinfo; // 或使用一个更通用的 Map<String, Object>

    // Getters and Setters
    public SituationPlot getPlot() {
        return plot;
    }

    public void setPlot(SituationPlot plot) {
        this.plot = plot;
    }

    public Map<String, Object> getPlotinfo() {
        return plotinfo;
    }

}
