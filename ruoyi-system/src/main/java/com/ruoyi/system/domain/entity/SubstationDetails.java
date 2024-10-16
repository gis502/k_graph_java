package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震前准备-地震灾害和救灾背景信息-描述与灾区及其临区地震活动和地震成灾特性直接相关的各类自然与社会人文信息-雅电集团变电站明细
    */
@Data
@TableName(value = "substation_details")
public class SubstationDetails {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 所属单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 所属班组
     */
    @TableField(value = "team")
    private String team;

    /**
     * 类型(必填：杆塔、变电站、生产生活场所)
     */
    @TableField(value = "\"type\"")
    private String type;

    /**
     * 电压等级(杆塔必填)
     */
    @TableField(value = "voltage_level")
    private String voltageLevel;

    /**
     * 名称(必填)
     */
    @TableField(value = "\"name\"")
    private String name;

    /**
     * 所属线路(杆塔必填)
     */
    @TableField(value = "line")
    private String line;

    /**
     * 风险隐患类别(低洼站所、临近河道、地灾风险、孤岛站所)
     */
    @TableField(value = "risk_category")
    private String riskCategory;

    /**
     * 类型划分(垮塌、崩塌、滑坡、沉降、塌陷、飞石、泥石流等)
     */
    @TableField(value = "type_division")
    private String typeDivision;

    /**
     * 所属政府公布的隐患点名称（填写政府公布点位名称）
     */
    @TableField(value = "government_disclosed_point")
    private String governmentDisclosedPoint;

    /**
     * 风险隐患简要描述
     */
    @TableField(value = "risk_description")
    private String riskDescription;

    /**
     * 区县
     */
    @TableField(value = "district")
    private String district;

    /**
     * 乡镇
     */
    @TableField(value = "town")
    private String town;

    /**
     * 村组
     */
    @TableField(value = "village")
    private String village;

    /**
     * 地理位置
     */
    @TableField(value = "address")
    private String address;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    private String remarks;

    /**
     * 数据来源单位
     */
    @TableField(value = "data_source_unit")
    private String dataSourceUnit;

    /**
     * 行政级别
     */
    @TableField(value = "administrative_level")
    private String administrativeLevel;

    /**
     * 创建时间
     */
    @TableField(value = "creation_time")
    private Date creationTime;

    /**
     * 编号

     */
    @TableField(value = "\"number\"")
    private Integer number;

    @TableField(value = "geom")
    private Object geom;
}