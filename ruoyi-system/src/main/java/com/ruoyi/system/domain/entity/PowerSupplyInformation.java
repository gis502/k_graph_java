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
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-生命线震害信息-供电系统表(用户上传数据)
    */
@Data
@TableName(value = "power_supply_information")
public class PowerSupplyInformation {
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
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "affected_area")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedArea;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    @ExcelProperty(value = {"交通电力通信-电力设施损毁及抢修情况统计表", "统计截止时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportingDeadline;



    /**
     * 累计停运变电站数量
     */
    @TableField(value = "total_out_of_service_substations")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "累计停运变（发）电站（座）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalOutOfServiceSubstations;

    /**
     * 已恢复变电站数量
     */
    @TableField(value = "restored_substations")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "已恢复变（发）电站（座）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer restoredSubstations;

    /**
     * 待修复变电站数量
     */
    @TableField(value = "to_be_repaired_substations")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "待修复变（发）电站（座）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer toBeRepairedSubstations;

    /**
     * 累计跳闸线路数量
     */
    @TableField(value = "total_trip_circuits")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "累计跳闸线路（条）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalTripCircuits;

    /**
     * 已恢复线路数量
     */
    @TableField(value = "restored_circuits")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "已恢复线路（条）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer restoredCircuits;

    /**
     * 待恢复线路数量
     */
    @TableField(value = "to_be_restored_circuits")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "待恢复线路（条）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer toBeRestoredCircuits;

    /**
     * 累计主网停电用户数
     */
    @TableField(value = "total_blackout_users")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "累计主网停电用户数（户）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalBlackoutUsers;

    /**
     * 已恢复主网供电用户数
     */
    @TableField(value = "restored_power_users")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "已恢复主网供电用户数（户）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer restoredPowerUsers;

    /**
     * 目前主网供电中断的村庄
     */
    @TableField(value = "currently_blacked_out_villages")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "目前主网供电中断村"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String currentlyBlackedOutVillages;

    /**
     * 应急供电用户数
     */
    @TableField(value = "emergency_power_users")
    @ExcelProperty({"交通电力通信-电力设施损毁及抢修情况统计表", "应急供电用户数（户）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer emergencyPowerUsers;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordTime;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime systemInsertTime;

}
