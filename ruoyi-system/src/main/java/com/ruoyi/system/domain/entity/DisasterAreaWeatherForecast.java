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
@TableName(value = "disaster_area_weather_forecast")
public class DisasterAreaWeatherForecast {


    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    @TableField(value = "earthquake_id")
    private String earthquakeId;

    @TableField(value = "earthquake_name")
    @ExcelProperty(value = {"气象情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"气象情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    @TableField(value = "affected_area_name")
    @ExcelProperty(value = {"气象情况", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedAreaName;

    @TableField("submission_deadline")
    @ExcelProperty(value = {"气象情况", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime submissionDeadline;

    @TableField(value = "weather_forecast")
    @ExcelProperty(value = {"气象情况", "灾区未来三天天气预报"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String weatherForecast;


    @TableField("record_time")
    private LocalDateTime recordTime;

    @TableField("system_insert_time")
    private LocalDateTime systemInsertTime;







}
