package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-应急避难功能区-室外型避难场所，室内型避难场所表
    */
@Data
@TableName(value = "emergency_shelter_function_area")
public class EmergencyShelterFunctionArea {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 场所名称
     */
    @TableField(value = "shelter_name")
    private String shelterName;

    /**
     * 场所级别：省级、市级、县级、乡镇（街道）级、村（社区）级
     */
    @TableField(value = "shelter_level")
    private String shelterLevel;

    /**
     * 场所功能：单一性、综合性、特定避难场所
     */
    @TableField(value = "shelter_function")
    private String shelterFunction;

    /**
     * 场所类型：紧急型（1天以内）、短期型（2-14天）、长期型（15-180天）
     */
    @TableField(value = "shelter_type")
    private String shelterType;

    /**
     * 避难人员数量：50人以下、50-100人、100-200人、200-500人、500人以上
     */
    @TableField(value = "shelter_capacity")
    private String shelterCapacity;

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