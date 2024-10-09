package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "building_damage")
public class BuildingDamage {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    @TableField(value = "eqid")
    private String eqid;

    @TableField(value = "county")
    private String county;

    @TableField(value = "size")
    private double size;
}
