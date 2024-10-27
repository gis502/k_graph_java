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
@TableName("red_cross_donations")
public class RedCrossDonations {

    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    @TableField(value = "earthquake_id")
    private String earthquakeId;

    @TableField(value = "earthquake_area_name")
    @ExcelProperty(value = {"红十字会系统接收捐赠资金", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"红十字会系统接收捐赠资金", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    @TableField(value = "earthquake_area_name")
    @ExcelProperty(value = {"红十字会系统接收捐赠资金", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeAreaName;

    @TableField(value = "submission_deadline")
    @ExcelProperty(value = {"红十字会系统接收捐赠资金", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime submissionDeadline;

    @TableField(value = "donation_amount")
    @ExcelProperty(value = {"红十字会系统接收捐赠资金", "当日"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Double donationAmount;

    @TableField(value = "record_time")
    private LocalDateTime recordTime;

}
