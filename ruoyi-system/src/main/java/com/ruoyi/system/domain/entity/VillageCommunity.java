package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震前准备-基础背景信息-村/社区
    */
@Data
@TableName(value = "village_community")
public class VillageCommunity {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 区划代码
     */
    @TableField(value = "district_code")
    private String districtCode;

    /**
     * 所属区/县名称
     */
    @TableField(value = "district_name")
    private String districtName;

    /**
     * 乡镇街道
     */
    @TableField(value = "town_street")
    private String townStreet;

    /**
     * 居委会
     */
    @TableField(value = "residents_committee")
    private String residentsCommittee;

    /**
     * 地理位置
     */
    @TableField(value = "geom")
    private Object geom;

    /**
     * 省
     */
    @TableField(value = "province")
    private String province;

    /**
     * 市
     */
    @TableField(value = "city")
    private String city;
}