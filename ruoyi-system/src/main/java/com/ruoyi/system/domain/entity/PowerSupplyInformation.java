package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
     * 累计停运变电站数量
     */
    @TableField(value = "total_out_of_service_substations")
    private Integer totalOutOfServiceSubstations;

    /**
     * 已恢复变电站数量
     */
    @TableField(value = "restored_substations")
    private Integer restoredSubstations;

    /**
     * 待修复变电站数量
     */
    @TableField(value = "to_be_repaired_substations")
    private Integer toBeRepairedSubstations;

    /**
     * 累计跳闸线路数量
     */
    @TableField(value = "total_trip_circuits")
    private Integer totalTripCircuits;

    /**
     * 已恢复线路数量
     */
    @TableField(value = "restored_circuits")
    private Integer restoredCircuits;

    /**
     * 待恢复线路数量
     */
    @TableField(value = "to_be_restored_circuits")
    private Integer toBeRestoredCircuits;

    /**
     * 累计主网停电用户数
     */
    @TableField(value = "total_blackout_users")
    private Integer totalBlackoutUsers;

    /**
     * 已恢复主网供电用户数
     */
    @TableField(value = "restored_power_users")
    private Integer restoredPowerUsers;

    /**
     * 目前主网供电中断的村庄
     */
    @TableField(value = "currently_blacked_out_villages")
    private String currentlyBlackedOutVillages;

    /**
     * 应急供电用户数
     */
    @TableField(value = "emergency_power_users")
    private Integer emergencyPowerUsers;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    private Date reportingDeadline;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    private Date earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;
}