package com.ruoyi.system.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 震前准备-基础背景信息-城市人口管理
 */
@Data
@TableName(value = "population_data")
public class PopulationData {
    /**
     * 唯一标识
     */
    @TableId(value = "uuid", type = IdType.INPUT)
    private Object uuid;

    /**
     * 区域名称
     */
    @TableField(value = "area")
    private String area;

    /**
     * 总人数
     */
    @TableField(value = "total_population")
    private String totalPopulation;

    /**
     * 数据更新时间
     */
    @TableField(value = "update_time")
    private String updateTime;


}
