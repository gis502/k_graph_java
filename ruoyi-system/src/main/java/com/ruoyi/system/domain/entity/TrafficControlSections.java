package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
    private String affectedArea;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    private Date recordTime;

    /**
     * 累计发放通行证数量
     */
    @TableField(value = "total_passes_issued")
    private Integer totalPassesIssued;

    /**
     * 设置的交通管制分流点数量
     */
    @TableField(value = "control_diversion_points")
    private Integer controlDiversionPoints;

    /**
     * 具体的交通管制路段
     */
    @TableField(value = "traffic_control_section")
    private String trafficControlSection;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    private Date reportingDeadline;

    /**
     * 系统自动插入记录的时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 地震发生时间
     */
    @TableField(value = "earthquake_time")
    private Date earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;
}