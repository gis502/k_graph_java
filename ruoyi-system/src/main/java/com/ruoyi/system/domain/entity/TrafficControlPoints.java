package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-交通设施破坏类-交通管制点
    */
@Data
@TableName(value = "traffic_control_points")
public class TrafficControlPoints {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 公路名称
     */
    @TableField(value = "road_name")
    private String roadName;

    /**
     * 公路等级：高速、国道、省道、县道、乡道、其它
     */
    @TableField(value = "road_level")
    private String roadLevel;

    /**
     * 管制类型：限行、禁行
     */
    @TableField(value = "control_type")
    private String controlType;

    /**
     * 管制单位
     */
    @TableField(value = "control_unit")
    private String controlUnit;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;
}