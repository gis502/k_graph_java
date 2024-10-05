package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-转移安置信息（用户上传数据）
    */
@Data
@TableName(value = "transfer_settlement_info")
public class TransferSettlementInfo {
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震名称
     */
    @ExcelProperty(value = {"转移安置信息", "地震名称"})
    @TableField(value = "earthquake_name")
    private String earthquakeName;

    /**
     * 编号
     */
    @TableField(value = "transfer_id")
    private String transferId;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 震级，地震 Richter 震级
     */
    @ExcelProperty({"转移安置信息", "震级"})
    @TableField(value = "magnitude")
    private String magnitude;

    /**
     * 震区名称
     */
    @ExcelProperty(value = {"转移安置信息", "震区（县/区）"})
    @TableField(value = "earthquake_area_name")
    private String earthquakeAreaName;

    /**
     * 启用应急避难场所（处）
     */
    @ExcelProperty(value = {"转移安置信息", " 启用应急避难场所（处）"})
    @TableField(value = "emergency_shelters")
    private Integer emergencyShelters;

    /**
     * 搭建临时安置点（处）
     */
    @ExcelProperty(value = {"转移安置信息", "搭建临时安置点（处）"})
    @TableField(value = "temporary_shelters")
    private Integer temporaryShelters;

    /**
     * 新增转移安置（人）
     */
    @ExcelProperty(value = {"转移安置信息", "新增转移安置（人）"})
    @TableField(value = "newly_transferred")
    private Integer newlyTransferred;

    /**
     * 累计转移安置（人）
     */
    @ExcelProperty(value = {"转移安置信息", "累计转移安置（人）"})
    @TableField(value = "cumulative_transferred")
    private Integer cumulativeTransferred;

    /**
     * 集中安置（人）
     */
    @ExcelProperty(value = {"转移安置信息", "集中安置（人）"})
    @TableField(value = "centralized_settlement")
    private Integer centralizedSettlement;

    /**
     * 分散安置（人）
     */
    @ExcelProperty(value = {"转移安置信息", "分散安置（人）"})
    @TableField(value = "distributed_settlement")
    private Integer distributedSettlement;

    /**
     * 填报截止时间
     */
    @ExcelProperty(value = {"转移安置信息", "统计截止时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "reporting_deadline")
    private LocalDateTime reportingDeadline;

    /**
     * 系统插入时间
     */
    @ExcelProperty(value = {"转移安置信息", "系统插入时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "system_inserttime")
    private LocalDateTime systemInserttime;

    /**
     * 地震时间
     */
    @ExcelProperty(value = {"转移安置信息", "地震时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "earthquake_time")
    private LocalDateTime earthquakeTime;


    public LocalDateTime getSystemInserttime() {
        return systemInserttime != null ? systemInserttime.truncatedTo(ChronoUnit.SECONDS) : null;
    }
}


