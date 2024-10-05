package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-生命线震害信息-道路损毁表（用户上传数据）
    */
@Data
@TableName(value = "road_damage")
public class RoadDamage {
    /**
     * 序号
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
     * 高速公路及国道损毁情况
     */
    @TableField(value = "highways_national_roads")
    private String highwaysNationalRoads;

    /**
     * 省道损毁情况
     */
    @TableField(value = "provincial_roads")
    private String provincialRoads;

    /**
     * 目前道路中断的村庄
     */
    @TableField(value = "villages_with_road_closures")
    private String villagesWithRoadClosures;

    /**
     * 正在抢修的道路
     */
    @TableField(value = "under_repair")
    private String underRepair;

    /**
     * 已恢复的道路
     */
    @TableField(value = "restored_roads")
    private String restoredRoads;

    /**
     * 累计协调的运力，单位为车辆数
     */
    @TableField(value = "total_coordinated_transport_capacity")
    private Integer totalCoordinatedTransportCapacity;

    /**
     * 累计损毁的道路长度，单位为公里
     */
    @TableField(value = "total_road_damage_km")
    private BigDecimal totalRoadDamageKm;

    /**
     * 已抢通的道路长度，单位为公里
     */
    @TableField(value = "restored_km")
    private BigDecimal restoredKm;

    /**
     * 待抢修的道路长度，单位为公里
     */
    @TableField(value = "pending_repair_km")
    private BigDecimal pendingRepairKm;

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