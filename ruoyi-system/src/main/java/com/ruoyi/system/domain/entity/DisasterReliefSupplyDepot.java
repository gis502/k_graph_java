package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-应急避难功能区-救灾物资储备库表
    */
@Data
@TableName(value = "disaster_relief_supply_depot")
public class DisasterReliefSupplyDepot {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 储备库名称
     */
    @TableField(value = "depot_name")
    private String depotName;

    /**
     * 储备库位置
     */
    @TableField(value = "depot_location")
    private String depotLocation;

    /**
     * 储备库级别：中央级(区域性)、省级、市级、县级、乡级
     */
    @TableField(value = "depot_level")
    private String depotLevel;

    /**
     * 物资类型：抢险救援保障物资、应急救援力量保障物资、基本生活保障物资
     */
    @TableField(value = "material_type")
    private String materialType;

    /**
     * 管护单位
     */
    @TableField(value = "management_unit")
    private String managementUnit;

    /**
     * 联系人员
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;
}