package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "military_arrows")
public class MilitaryArrows {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    @TableField(value = "plot_id")
    private String plotId;

    @TableField(value = "starting_point")
    private String startingPoint;

    @TableField(value = "target_point")
    private String targetPoint;

    @TableField(value = "action_content")
    private String actionContent;
}