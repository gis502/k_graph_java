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
import java.util.Date;

@Data
@TableName("supply_water")
public class SupplyWater {

    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震标识id
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;
    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"保障安置供水情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;
    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty({"保障安置供水情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"保障安置供水情况", "震区名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeAreaName;

    /**
     * 填报截止时间
     */
    @TableField(value = "report_deadline")
    @ExcelProperty({"保障安置供水情况", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportDeadline;

    /**
     * 集中供水工程受损（处）
     */
    @TableField(value = "water_supply_points")
    @ExcelProperty({"保障安置供水情况", "保障安置点供水（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer waterSupplyPoints;


    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;


}
