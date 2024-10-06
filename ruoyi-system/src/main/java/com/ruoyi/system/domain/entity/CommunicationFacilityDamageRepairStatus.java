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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-通信设施损毁及抢修情况（用户上传数据）
    */
@Data
@TableName(value = "communication_facility_damage_repair_status")
public class CommunicationFacilityDamageRepairStatus {
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
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime earthquakeTime;


    /**
     * 震区名称
     */
    @TableField(value = "earthquake_zone_name")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "震区名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeZoneName;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportingDeadline;

    /**
     * 总停用基站数
     */
    @TableField(value = "total_disabled_base_stations")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "总停用基站数"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalDisabledBaseStations;

    /**
     * 已恢复基站数
     */
    @TableField(value = "restored_base_stations")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "已恢复基站数"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer restoredBaseStations;

    /**
     * 当前停用基站数
     */
    @TableField(value = "current_disabled_base_stations")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "当前停用基站数"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentDisabledBaseStations;

    /**
     * 总损坏光缆长度
     */
    @TableField(value = "total_damaged_cable_length")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "总损坏光缆长度"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal totalDamagedCableLength;

    /**
     * 已修复光缆长度
     */
    @TableField(value = "repaired_cable_length")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "已修复光缆长度"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal repairedCableLength;

    /**
     * 当前待修复光缆长度
     */
    @TableField(value = "current_pending_repair_cable_length")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "当前待修复光缆长度"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal currentPendingRepairCableLength;

    /**
     * 当前通信中断村庄数
     */
    @TableField(value = "current_interrupted_villages_count")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "当前通信中断村庄数"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentInterruptedVillagesCount;

    /**
     * 当前通信中断影响数
     */
    @TableField(value = "current_interrupted_impact_count")
    @ExcelProperty(value = {"通信设施损毁及抢修情况", "当前通信中断影响数"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentInterruptedImpactCount;


    /**
     * 系统插入时间
     */
    @TableField(value = "system_insertion_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime systemInsertionTime;

}
