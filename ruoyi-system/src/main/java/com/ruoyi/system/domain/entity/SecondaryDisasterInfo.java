package com.ruoyi.system.domain.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "secondary_disaster_info")
public class SecondaryDisasterInfo {

    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /*
     * 地震名称
     * */
    @TableField(value = "earthquake_name")
    @ExcelProperty(value = {"山洪危险区情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /*
     * 地震时间
     * */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"山洪危险区情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    @TableField(value = "affected_area")
    @ExcelProperty(value = {"山洪危险区情况", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedArea;

    @TableField(value = "reporting_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = {"山洪危险区情况", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportingDeadline;

    @TableField(value = "hazard_points")
    @ExcelProperty(value = {"山洪危险区情况", "隐患点（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer hazardPoints;

    @TableField(value = "threatened_areas_secondary")
    @ExcelProperty(value = {"山洪危险区情况", "受威胁地区（乡镇、村）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String threatenedAreasSecondary;

    @TableField(value = "threatened_population_secondary")
    @ExcelProperty(value = {"山洪危险区情况", "受威胁群众（户或人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String threatenedPopulationSecondary;

    @TableField(value = "evacuation_secondary")
    @ExcelProperty(value = {"山洪危险区情况", "避险转移（户或人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String evacuationSecondary;



    @TableField(value = "barrier_lake")
    private String barrierLake;

    @TableField(value = "threatened_areas")
    private String threatenedAreas;

    @TableField(value = "threatened_population")
    private Integer threatenedPopulation;

    @TableField(value = "evacuation")
    private Integer evacuation;

    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;

    @TableField(value = "record_time")
    private LocalDateTime recordTime;

}
