package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-地震震情信息-余震信息（用户上传数据）
    */
@Data
@TableName(value = "aftershock_information")
public class AftershockInformation {
    /**
     * 序号，自增主键
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震标识，标识地震事件的唯一标识符
     */
    @TableField(value = "earthquake_identifier")
    private String earthquakeIdentifier;

    /**
     * 地震名称，地震的描述性名称
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "地震名称"})
    @TableField(value = "earthquake_name")
    private String earthquakeName;

    /**
     * 余震次数累计，所有余震的次数总和
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "余震次数累计"})
    @TableField(value = "total_aftershocks")
    private Integer totalAftershocks;

    /**
     * 3.0-3.9级余震次数，该级别范围内的余震次数
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "3.0-3.9级余震次数"})
    @TableField(value = "magnitude_3_3_9")
    private Integer magnitude339;

    /**
     * 4.0-4.9级余震次数，该级别范围内的余震次数
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "4.0-4.9级余震次数"})
    @TableField(value = "magnitude_4_4_9")
    private Integer magnitude449;

    /**
     * 5.0-5.9级余震次数，该级别范围内的余震次数
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "5.0-5.9级余震次数"})
    @TableField(value = "magnitude_5_5_9")
    private Integer magnitude559;

    /**
     * 系统插入时间，记录被系统创建的时间
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "系统插入时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 填报截至时间，报告提交的最终期限
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "填报截至时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "submission_deadline")
    private Date submissionDeadline;

    /**
     * 震区，受影响的地区名称
     */
    @TableField(value = "affected_area")
    private String affectedArea;

    /**
     * 地震时间，地震发生的具体时间
     */
    @ExcelProperty(value = {"震情灾情-余震信息", "地震时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "earthquake_time")
    private Date earthquakeTime;

    /**
     * 余震分布，描述余震的空间分布情况
     */
    @TableField(value = "aftershock_distribution")
    private String aftershockDistribution;

    /**
     * 地震强度，余震的强度级别
     */
    @TableField(value = "earthquake_intensity")
    private Double earthquakeIntensity;

    /**
     * 持续时间，余震持续的时间段
     */
    @TableField(value = "duration")
    private String duration;

    /**
     * 影响范围，余震影响的地理范围面积
     */
    @TableField(value = "affected_range")
    private String affectedRange;

    /**
     * 与主震的关系，描述余震与主震的关联性
     */
    @TableField(value = "relationship_with_mainshock")
    private String relationshipWithMainshock;
}
