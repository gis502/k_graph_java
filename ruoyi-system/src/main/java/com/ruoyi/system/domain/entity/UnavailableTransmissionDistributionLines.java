package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-生命线工程破坏类-不可用输、配电线路
    */
@Data
@TableName(value = "unavailable_transmission_distribution_lines")
public class UnavailableTransmissionDistributionLines {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 供电区域
     */
    @TableField(value = "supply_area")
    private String supplyArea;

    /**
     * 电压等级
     */
    @TableField(value = "voltage_level")
    private String voltageLevel;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
