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



/**
 * 救援力量情况统计表(用户上传数据)
 */
@Data
@TableName(value = "rescue_forces")
public class RescueForces {
    /**
     * 序号，自增主键
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
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeAreaName;

    /**
     * 统计截止时间
     */
    @TableField(value = " submission_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private LocalDateTime  submissionDeadline;

    /**
     * 解放军
     */
    @TableField(value = "pla_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "解放军"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer plaCount;

    /**
     * 武警部队
     */
    @TableField(value = "armed_police_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "武警部队"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer armedPoliceCount;

    /**
     * 民兵
     */
    @TableField(value = "militia_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "民兵"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer militiaCount;

    /**
     * 消防救援
     */
    @TableField(value = "fire_rescue_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "消防救援"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer fireRescueCount;

    /**
     * 森林消防
     */
    @TableField(value = "forest_fire_rescue_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "森林消防"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer forestFireRescueCount;

    /**
     * 专业力量：安能、…
     */
    @TableField(value = "professional_forces_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "专业力量：安能、…"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String professionalForcesCount;

    /**
     * 应急安全生产
     */
    @TableField(value = "emergency_production_safety_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "应急安全生产"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer emergencyProductionSafetyCount;

    /**
     * 医疗救援
     */
    @TableField(value = "medical_rescue_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "医疗救援"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer medicalRescueCount;

    /**
     * 交通通信电力等力量
     */
    @TableField(value = "transportation_communication_power_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "交通通信电力等力量"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer transportationCommunicationPowerCount;

    /**
     * 空中救援
     */
    @TableField(value = "air_rescue_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "空中救援"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer airRescueCount;

    /**
     * 志愿抢险队
     */
    @TableField(value = "volunteer_rescue_team_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "志愿者抢修队"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer volunteerRescueTeamCount;

    /**
     * 党员突击队
     */
    @TableField(value = "party_member_commando_count")
    @ExcelProperty({"力量物资资金-救援力量情况统计表", "党员突击队"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer partyMemberCommandoCount;

    /**
     * 直升机
     */
    @TableField(value = "helicopter_count")
    private Integer helicopterCount;

    /**
     * 舟桥
     */
    @TableField(value = "bridge_boat_count")
    private Integer bridgeBoatCount;

    /**
     * 翼龙无人机
     */
    @TableField(value = "wing_drone_count")
    private Integer wingDroneCount;

    /**
     * 当前救灾物资总数（万件）
     */
    @TableField(value = "current_disaster_supplies_total")
    private Integer currentDisasterSuppliesTotal;

    /**
     * 调拨安置类物资（万件）
     */
    @TableField(value = "allocated_supplies_total")
    private Integer allocatedSuppliesTotal;

    /**
     * 帐篷（顶）
     */
    @TableField(value = "tents_count")
    private Integer tentsCount;

    /**
     * 棉被（床）
     */
    @TableField(value = "quilts_count")
    private Integer quiltsCount;

    /**
     * 折叠床（张）
     */
    @TableField(value = "folding_beds_count")
    private Integer foldingBedsCount;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    private LocalDateTime recordTime;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;

}
