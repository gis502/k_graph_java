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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-震情伤亡-转移安置统计表（用户上传数据）
    */
@Data
@TableName(value = "transfer_settlement_info")
public class TransferSettlementInfo {
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震名称
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "地震名称"})
    @TableField(value = "earthquake_name")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;
    /**
     * 地震时间
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "earthquake_time")
    private LocalDateTime earthquakeTime;
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
    @ExcelProperty({"震情伤亡-转移安置统计表", "震级"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "magnitude")
    private String magnitude;

    /**
     * 震区名称
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "earthquake_area_name")
    private String earthquakeAreaName;

    /**
     * 填报截止时间
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "reporting_deadline")
    private LocalDateTime reportingDeadline;
    /**
     * 启用应急避难场所（处）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", " 启用应急避难场所（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "emergency_shelters")
    private Integer emergencyShelters;

    /**
     * 搭建临时安置点（处）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "搭建临时安置点（处）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "temporary_shelters")
    private Integer temporaryShelters;

    /**
     * 新增转移安置（人）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "新增转移安置（人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "newly_transferred")
    private Integer newlyTransferred;

    /**
     * 累计转移安置（人）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "累计转移安置（人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "cumulative_transferred")
    private Integer cumulativeTransferred;

    /**
     * 集中安置（人）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "集中安置（人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "centralized_settlement")
    private Integer centralizedSettlement;

    /**
     * 分散安置（人）
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "分散安置（人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @TableField(value = "distributed_settlement")
    private Integer distributedSettlement;


    /**
     * 系统插入时间
     */
    @ExcelProperty(value = {"震情伤亡-转移安置统计表", "系统插入时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "system_inserttime")
    private LocalDateTime systemInserttime;




    public LocalDateTime getSystemInserttime() {
        return systemInserttime != null ? systemInserttime.truncatedTo(ChronoUnit.SECONDS) : null;
    }
}


