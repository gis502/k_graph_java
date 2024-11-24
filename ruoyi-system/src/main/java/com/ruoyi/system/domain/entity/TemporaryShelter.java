package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-灾民救助信息-应急避难功能区-临时避险安置点表
    */
@Data
@TableName(value = "temporary_shelter")
public class TemporaryShelter {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 安置点名称
     */
    @TableField(value = "shelter_name")
    private String shelterName;

    /**
     * 安置点类型：帐篷、板房、其他
     */
    @TableField(value = "shelter_type")
    private String shelterType;

    /**
     * 基础保障：通路、通电、通水、通网
     */
    @TableField(value = "basic_facilities")
    private String basicFacilities;

    /**
     * 容纳人数：20人以下、20-50人、50-100人、100人以上
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

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
