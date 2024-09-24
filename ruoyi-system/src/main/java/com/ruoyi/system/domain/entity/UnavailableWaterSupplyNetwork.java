package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-生命线工程破坏类-不可用供水管网
    */
@Data
@TableName(value = "unavailable_water_supply_network")
public class UnavailableWaterSupplyNetwork {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 供水区域
     */
    @TableField(value = "water_supply_area")
    private String waterSupplyArea;

    /**
     * 供水管网类型
     */
    @TableField(value = "water_supply_network_type")
    private String waterSupplyNetworkType;
}