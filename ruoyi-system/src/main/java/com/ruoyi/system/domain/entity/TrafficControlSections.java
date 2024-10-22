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

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-生命线震害信息-交通管制路段表（用户上传数据）
 * @author 方
 */
@Data
@TableName(value = "traffic_control_sections")
public class TrafficControlSections {
    /**
     * 编号
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;
    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震发生时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "affected_area")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedArea;

    /**
     * 统计截止时间
     */
    @TableField(value = "reporting_deadline")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "统计截止时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportingDeadline;

    /**
     * 累计发放通行证数量
     */
    @TableField(value = "total_passes_issued")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "累计发放通行证（张）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalPassesIssued;

    /**
     * 设置的交通管制分流点数量
     */
    @TableField(value = "control_diversion_points")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "设置管制分流点（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer controlDiversionPoints;

    /**
     * 具体的交通管制路段
     */
    @TableField(value = "traffic_control_section")
    @ExcelProperty({"交通电力通信-交通管控情况统计表", "交通管制路段"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String trafficControlSection;


    /**
     * 系统自动插入记录的时间
     */
    @TableField(value = "system_insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime systemInsertTime;


}
