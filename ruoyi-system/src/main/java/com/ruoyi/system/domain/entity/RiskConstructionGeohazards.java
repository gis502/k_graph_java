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
@TableName(value = "risk_construction_geohazards")
public class RiskConstructionGeohazards {


    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;
    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;


    /*
     * 地震名称
     * */
    @TableField(value = "earthquake_name")
    @ExcelProperty(value = {"地质灾害情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /*
     * 地震时间
     * */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"地质灾害情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /*
    * 震区名称
    * */
    @TableField(value = "quake_area_name")
    @ExcelProperty(value = {"地质灾害情况", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String quakeAreaName;

    /*
     * 统计截止时间
     * */
    @TableField(value = "report_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = {"地质灾害情况", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportDeadline;


    /*
    * 现有风险点
    * */
    @TableField(value = "existing_risk_points")
    @ExcelProperty(value = {"地质灾害情况", "既有隐患点（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer existingRiskPoints;
    /*
    * 新增风险点
    * */
    @TableField(value = "new_risk_points")
    @ExcelProperty(value = {"地质灾害情况", "新增隐患点（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer newRiskPoints;
    /*
    * 正在施工点数量
    * */
    @TableField(value = "construction_points")
    @ExcelProperty(value = {"地质灾害情况", "排查在建工程（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer constructionPoints;
    /*
    * 基础设施检查点数量
    * */
    @TableField(value = "infrastructure_checkpoints")
    @ExcelProperty(value = {"地质灾害情况", "排查基础设施（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer infrastructureCheckpoints;
    /*
    * 报警数量
    * */
    @TableField(value = "alarm_count")
    @ExcelProperty(value = {"地质灾害情况", "预警发布（次）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer alarmCount;
    /*
    * 疏散数量
    * */
    @TableField(value = "evacuation_count")
    @ExcelProperty(value = {"地质灾害情况", "转移避险（人次）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer evacuationCount;


    /*
    * 系统插入时间
    * */
    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;


}
