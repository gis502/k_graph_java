package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-生命线工程破坏类-不可用输气管线
    */
@Data
@TableName(value = "unusable_gas_pipes")
public class UnusableGasPipes {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 供气区域
     */
    @TableField(value = "supply_area")
    private String supplyArea;

    /**
     * 管道分类
     */
    @TableField(value = "pipe_type")
    private String pipeType;

    /**
     * 管理单位
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
     * 图片
     */
    @TableField(value = "image")
    private Object image;

    /**
     * 时间
     */
    @TableField(value = "\"timestamp\"")
    private Date timestamp;

    /**
     * 位置
     */
    @TableField(value = "geom")
    private Object geom;

    /**
     * 破坏描述
     */
    @TableField(value = "damage_description")
    private String damageDescription;
}