package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "district_economy")
public class DistrictEconomy {
    /**
     * 区县
     */
    @TableField(value = "county_district")
    private String countyDistrict;

    /**
     * 区县经济
     */
    @TableField(value = "district_economy")
    private String districtEconomy;

    /**
     * 经济对应年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 经济对应年份
     */
    @TableField(value = "highest_altitude")
    private String highestAltitude;

    /**
     * 经济对应年份
     */
    @TableField(value = "minimum_altitude")
    private String minimumAltitude;

    /**
     * 经济对应年份
     */
    @TableField(value = "terrain")
    private String terrain;

    /**
     * 经济对应年份
     */
    @TableField(value = "landform")
    private String landform;

}
