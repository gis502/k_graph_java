package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.dto.PlotRequest;
import com.ruoyi.system.domain.entity.SituationPlot;
import com.ruoyi.system.service.SituationPlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 功能：
 * 作者：邵文博
 * 日期：2024/9/30 17:06
 */
@RestController
@RequestMapping("/system/ploy")
public class SituationPlotController {
    @Autowired
    SituationPlotService situationPlotService;

    /**
     * @description: "插入标绘点和标绘点基本信息"
     * @author: SWB
     * @time: 2024/9/30 23:09
     **/
    @PostMapping("/insertplotandinfo")
    @Log(title = "态势标绘", businessType = BusinessType.INSERT)
    public ResponseEntity<String> addPlot(@RequestBody PlotRequest requestBody) {
        SituationPlot plot = requestBody.getPlot();
        Map<String, Object> plotInfo = requestBody.getPlotinfo();
        // 将 plotid 字符串转换为 UUID
        plot.setPlotId(UUID.fromString(plot.getPlotId().toString()));
        // 打印整个请求体
        System.out.println("Plot: " + plot);
        System.out.println("Plot Info: " + plotInfo);
        situationPlotService.save(plot);
        if (requestBody.getPlotinfo().size() > 1) {
            // 调用 service 层的 addPlot 方法
            situationPlotService.addPlot(plot.getPlotType(), plotInfo);
            return ResponseEntity.ok("Plot added successfully");
        } else {
            return ResponseEntity.ok("Plot added successfully");
        }

    }

    /**
     * @description: "更新标绘点和基本信息"
     * @author: SWB
     * @time: 2024/9/30 23:10
     **/
    @PutMapping("/updataplotinfo")
    @Log(title = "态势标绘", businessType = BusinessType.UPDATE)
    public ResponseEntity<String> updatePlotDetails(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String plotType,
            @RequestParam String plotId,
            @RequestBody Map<String, Object> details) {

        try {
            situationPlotService.updatePlotDetails(startTime, endTime, plotType, plotId, details);
            return ResponseEntity.ok("Plot details updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating plot details: " + e.getMessage());
        }
    }

    /**
     * @description: "根据plotId和plotType获取标绘点信息"
     * @author: SWB
     * @time: 2024/9/30 23:16
     **/
    @GetMapping("/getplotinfo")
    public Object getPlotDetails(@RequestParam String plotId, @RequestParam String plotType) {
        try {
            Object details = situationPlotService.getPlotInfos(plotType, plotId);
            if (details != null) {
                return details;
            } else {
                return "No details found for the given plotId and plotType";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while querying details: " + e.getMessage();
        }
    }

    /**
     * @description: "根据plotIds和plotTypes获取指定地震所有标绘点信息"
     * @author: NTY
     * @time: 2024/10/21
     **/
    @PostMapping("/getExcelPlotInfo")
    public Object getExcelPlotInfo(@RequestBody Map<String, List<String>> params) {
        List<String> plotIds = params.get("plotIds");
        List<String> plotTypes = params.get("plotTypes");

        try {
            if (plotIds.size() != plotTypes.size()) {
                return "plotIds and plotTypes must have the same length.";
            }

            // 查询多条标绘数据
            List<Object> ExcelPlotInfoList = situationPlotService.getExcelPlotInfo(plotTypes, plotIds);
            return !ExcelPlotInfoList.isEmpty() ? ExcelPlotInfoList : Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while querying details: " + e.getMessage();
        }
    }

    /**
     * @description: "删除标绘点以及对应的属性信息"
     * @author: SWB
     * @time: 2024/9/30 23:17
     **/
    // 删除 plot 和 details
    @DeleteMapping("/deleteplotinfo")
    @Log(title = "态势标绘", businessType = BusinessType.DELETE)
    public ResponseEntity<String> deletePlot(@RequestParam String plotType,
                                             @RequestParam String plotId) {
        try {
            situationPlotService.deletePlot(plotType, plotId);
            return ResponseEntity.ok("Plot and details deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the plot and details.");
        }
    }

    /*
     * @description: "根据eqid获取相应标绘信息"
     * @author: SWB
     * @time: 2024/10/4 16:24
     **/
    @GetMapping("/getplot")
    public List<SituationPlot> getPlot(String eqid) {
        List<SituationPlot> plotData = situationPlotService.getPlot(eqid);
        return plotData;
    }

    /**
     * @description: "时间轴或者大屏根据标会时间获取数据"
     * @author: SWB
     * @time: 2024/10/5 14:49
     **/
    @PostMapping("/getplotswithtime")
    public List<SituationPlot> getPlotsByEqId(@RequestParam String eqid) {
        List<SituationPlot> selectPlotWithTimeforEqid = situationPlotService.getSituationPlotsByEqId(eqid);
        return selectPlotWithTimeforEqid;
    }
}
