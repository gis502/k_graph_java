package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "economic_loss")
public class EconomicLoss {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    @TableField(value = "eqid")
    private String eqid;

    @TableField(value = "county")
    private String county;

    @TableField(value = "amount")
    private double amount;
}
