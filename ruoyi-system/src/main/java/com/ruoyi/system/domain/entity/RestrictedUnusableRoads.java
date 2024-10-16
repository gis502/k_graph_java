package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-交通设施破坏类-限制通行公路、不可通行公路
    */
@Data
@TableName(value = "restricted_unusable_roads")
public class RestrictedUnusableRoads {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 公路名称
     */
    @TableField(value = "road_name")
    private String roadName;

    /**
     * 公路级别：高速、国道、省道、县道、乡道、其它
     */
    @TableField(value = "road_level")
    private String roadLevel;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;

}
