package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震前准备-救灾能力储备信息-应急保障物资
    */
@Data
@TableName(value = "emergency_supply")
public class EmergencySupply {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 物资标识
     */
    @TableField(value = "material_id")
    private String materialId;

    /**
     * 物资类型名称
     */
    @TableField(value = "material_type_name")
    private String materialTypeName;

    /**
     * 物资名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 物资数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 计量单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 物资描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 物资规格
     */
    @TableField(value = "specification")
    private String specification;

    /**
     * 存放地点
     */
    @TableField(value = "storage_location")
    private String storageLocation;

    /**
     * 负责人
     */
    @TableField(value = "person_in_charge")
    private String personInCharge;

    /**
     * 应急电话
     */
    @TableField(value = "emergency_phone")
    private String emergencyPhone;

    /**
     * 应急手机
     */
    @TableField(value = "emergency_mobile")
    private String emergencyMobile;

    /**
     * 主管单位地址
     */
    @TableField(value = "supervising_unit_address")
    private String supervisingUnitAddress;

    /**
     * 保质期
     */
    @TableField(value = "shelf_life")
    private String shelfLife;

    /**
     * 到期日期
     */
    @TableField(value = "expiration_date")
    private String expirationDate;

    /**
     * 运输方式
     */
    @TableField(value = "transportation_mode")
    private String transportationMode;

    /**
     * 起运站点
     */
    @TableField(value = "departure_station")
    private String departureStation;

    /**
     * 位置
     */
    @TableField(value = "\"location\"")
    private Object location;

    /**
     * 机构名称
     */
    @TableField(value = "organization_name")
    private String organizationName;

    /**
     * 密级名称
     */
    @TableField(value = "confidentiality_level_name")
    private String confidentialityLevelName;

    /**
     * 级别名称
     */
    @TableField(value = "level_name")
    private String levelName;

    /**
     * 行政区划名称
     */
    @TableField(value = "administrative_division_name")
    private String administrativeDivisionName;

    /**
     * 数据来源单位名称
     */
    @TableField(value = "data_source_unit_name")
    private String dataSourceUnitName;
}