package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
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
    */
@Data
@TableName(value = "traffic_control_sections")
public class TrafficControlSections {
    /**
     * 编号
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 震区名称
     */
    @TableField(value = "affected_area")
    @ExcelProperty({"交通管控情况", "震区（县/区）"})
    private String affectedArea;

    /**
     * 累计发放通行证数量
     */
    @TableField(value = "total_passes_issued")
    @ExcelProperty({"交通管控情况", "累计发放通行证（张）"})
    private Integer totalPassesIssued;

    /**
     * 设置的交通管制分流点数量
     */
    @TableField(value = "control_diversion_points")
    @ExcelProperty({"交通管控情况", "设置管制分流点（处）"})
    private Integer controlDiversionPoints;

    /**
     * 具体的交通管制路段
     */
    @TableField(value = "traffic_control_section")
    @ExcelProperty({"交通管控情况", "交通管制路段（条）"})
    private String trafficControlSection;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reportingDeadline;

    /**
     * 系统自动插入记录的时间
     */
    @TableField(value = "system_insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime systemInsertTime;

    /**
     * 地震发生时间
     */
    @TableField(value = "earthquake_time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;
}
