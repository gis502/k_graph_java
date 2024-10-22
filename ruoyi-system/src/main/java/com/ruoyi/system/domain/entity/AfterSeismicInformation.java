package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-震后受灾区域和人数统计（用户上传数据）
    */
@Data
@TableName(value = "after_seismic_information")
public class AfterSeismicInformation {
    /**
     * 唯一标识UUID
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震标识，标识地震事件的唯一标识符
     */
    @TableField(value = "earthquake_identifier")
    private String earthquakeIdentifier;

    /**
     * 地震名称，地震的描述性名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"震情伤亡-震情受灾统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震时间，地震发生的具体时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty({"震情伤亡-震情受灾统计表", "地震时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震级，地震 Richter 震级
     */
    @ExcelProperty({"震情伤亡-震情受灾统计表", "震级"})
    @TableField(value = "magnitude")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String magnitude;

    /**
     * 震区名称，受影响地区的名称
     */
    @TableField(value = "affected_area_name")
    @ExcelProperty({"震情伤亡-震情受灾统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedAreaName;
    /**
     * 填报截止时间，报告提交的最终期限
     */
    @TableField(value = "submission_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = {"震情伤亡-震情受灾统计表", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime submissionDeadline;
    /**
     * 系统插入时间，记录被系统创建的时间
     */
    @TableField(value = "system_insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime systemInsertTime;

    /**
     * 受灾县（区）乡镇、村（社区）数量累计
     */
    @TableField(value = "affected_county_district")
    @ExcelProperty({"震情伤亡-震情受灾统计表", "受灾乡镇、村（社区）数量累计"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer affectedCountyDistrict;


    /**
     * 受灾人数累计（人）
     */
    @TableField(value = "affected_population")
    @ColumnWidth(30)
    @ExcelProperty({"震情伤亡-震情受灾统计表", "受灾人数累计（人）"})
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer affectedPopulation;
}
