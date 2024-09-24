package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-应急避难功能区-常备避险安置点表
    */
@Data
@TableName(value = "permanent_shelter")
public class PermanentShelter {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 置点名称
     */
    @TableField(value = "shelter_name")
    private String shelterName;

    /**
     * 安置点类型：村活动室、学校、卫生院、企业厂房、其他
     */
    @TableField(value = "shelter_type")
    private String shelterType;

    /**
     * 功能区：住宿区、餐饮区、清洗盥洗区、物资储备室、学习区、母婴室、文体活动区、其他
     */
    @TableField(value = "function_area")
    private String functionArea;

    /**
     * 基础保障：通路、通电、通水、通网
     */
    @TableField(value = "basic_facilities")
    private String basicFacilities;

    /**
     * 容纳人数：50人以下、50-100人、100-200人、200人以上
     */
    @TableField(value = "capacity")
    private String capacity;

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