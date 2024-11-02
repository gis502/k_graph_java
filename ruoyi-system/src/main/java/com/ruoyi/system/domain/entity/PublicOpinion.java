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
@TableName("public_opinion")
public class PublicOpinion {


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
    @ExcelProperty(value = {"宣传舆论", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /*
     * 地震时间
     * */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"宣传舆论", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    @TableField(value = "earthquake_zone_name")
    @ExcelProperty(value = {"宣传舆论", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeZoneName;

    @TableField(value = "submission_deadline")
    @ExcelProperty(value = {"宣传舆论", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime submissionDeadline;

    @TableField(value = "publicity_report")
    @ExcelProperty(value = {"宣传舆论", "宣传报道（篇）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer publicityReport;

    @TableField(value = "provincial_media_report")
    @ExcelProperty(value = {"宣传舆论", "中省主要媒体报道（篇）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer provincialMediaReport;

    @TableField(value = "public_opinion_risk_warning")
    @ExcelProperty(value = {"宣传舆论", "舆情风险提示（条）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer publicOpinionRiskWarning;

    @TableField(value = "press_conference")
    @ExcelProperty(value = {"宣传舆论", "发布会（场）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer pressConference;

    @TableField(value = "negative_opinion_disposal")
    @ExcelProperty(value = {"宣传舆论", "处置负面舆论（条）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer negativeOpinionDisposal;

    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;


}
