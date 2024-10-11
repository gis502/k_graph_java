package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-供水情况（用户上传数据）
    */
@Data
@TableName(value = "supply_situation")
public class SupplySituation {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震标识id
     */
    @TableField(value = "earthquake_id")
    private Integer earthquakeId;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    private String earthquakeAreaName;

    /**
     * 保障安置点供水（处）
     */
    @TableField(value = "water_supply_points")
    private Integer waterSupplyPoints;

    /**
     * 集中供水工程受损（处）
     */
    @TableField(value = "centralized_water_project_damage")
    private Integer centralizedWaterProjectDamage;

    /**
     * 填报截止时间
     */
    @TableField(value = "report_deadline")
    private Date reportDeadline;

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