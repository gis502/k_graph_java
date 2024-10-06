package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-通信设施损毁及抢修情况（用户上传数据）
    */
@Data
@TableName(value = "communication_facility_damage_repair_status")
public class CommunicationFacilityDamageRepairStatus {
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
    @TableField(value = "earthquake_zone_name")
    @ExcelProperty({"通信设施损毁及抢修情况", "震区（县/区）"})
    private String earthquakeZoneName;

    /**
     * 总停用基站数
     */
    @TableField(value = "total_disabled_base_stations")
    @ExcelProperty({"通信设施损毁及抢修情况", "累计退服基站（个）"})
    private Integer totalDisabledBaseStations;

    /**
     * 已恢复基站数
     */
    @TableField(value = "restored_base_stations")
    @ExcelProperty({"通信设施损毁及抢修情况", "抢通恢复基站（个）"})
    private Integer restoredBaseStations;

    /**
     * 当前停用基站数
     */
    @TableField(value = "current_disabled_base_stations")
    @ExcelProperty({"通信设施损毁及抢修情况", "目前退服基站（个）"})
    private Integer currentDisabledBaseStations;

    /**
     * 总损坏光缆长度
     */
    @TableField(value = "total_damaged_cable_length")
    @ExcelProperty({"通信设施损毁及抢修情况", "累计受损光缆  （公里）"})
    private BigDecimal totalDamagedCableLength;

    /**
     * 已修复光缆长度
     */
    @TableField(value = "repaired_cable_length")
    @ExcelProperty({"通信设施损毁及抢修情况", "抢通恢复光缆（公里）"})
    private BigDecimal repairedCableLength;

    /**
     * 当前待修复光缆长度
     */
    @TableField(value = "current_pending_repair_cable_length")
    @ExcelProperty({"通信设施损毁及抢修情况", "目前待修复光缆 （公里）"})
    private BigDecimal currentPendingRepairCableLength;

    /**
     * 当前通信中断村庄数
     */
    @TableField(value = "current_interrupted_villages_count")
    @ExcelProperty({"通信设施损毁及抢修情况", "目前通信中断村（个）"})
    private Integer currentInterruptedVillagesCount;

    /**
     * 当前通信中断影响数
     */
    @TableField(value = "current_interrupted_impact_count")
    private Integer currentInterruptedImpactCount;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    @ExcelProperty({"通信设施损毁及抢修情况", "统计截止时间"})
    private LocalDateTime reportingDeadline;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insertion_time")
    private LocalDateTime systemInsertionTime;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    private LocalDateTime earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;
}
