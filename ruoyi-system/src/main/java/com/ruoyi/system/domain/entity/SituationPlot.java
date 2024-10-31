package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

/**
    * 震后生成-态势标绘表
    */
@Data
@TableName(value = "situation_plot")
public class SituationPlot {

    /**
     * 地震ID
     */
     @TableField(value = "earthquake_id")
     private String earthquakeId;

    /**
     * 标绘ID (唯一标识符)
     */
    @TableId(value = "plot_id", type = IdType.AUTO)
    private Object plotId;

    /**
     * 点、线、面三种其一
     */
    @TableField(value = "drawtype")
    private String drawtype;

    /**
     * 态势标绘的图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 程度(滑坡数量、救援队伍数量、人员伤亡)
     */
    @TableField(value = "severity")
    private String severity;

    /**
     * 态势标绘的类型
     */
    @TableField(value = "plot_type")
    private String plotType;

    /**
     * 角度
     */
    @TableField(value = "angle")
    private BigDecimal angle;


    /**
     * 生成的时间（灾难发生时间如果在标绘时间之前可以在后台修改）
     */
    @TableField(value = "creation_time")
    private LocalDateTime creationTime;

    /**
     * 高程
     */
    @TableField(value = "elevation")
    private BigDecimal elevation;

    /**
     * 标绘开始时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 标绘结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 是否删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 位置
     */
    @TableField(value = "geom")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL) // 仅序列化非空字段
    private Geometry geom;

    @TableField(exist = false) // Indicates no corresponding database column
    private Double longitude;

    @TableField(exist = false) // Indicates no corresponding database column
    private Double latitude;

    // 设置 geom 时提取经纬度
    public void setGeom(Geometry geom) {
        this.geom = geom;
        if (geom != null) {
            this.longitude = geom.getCoordinate().x;
            this.latitude = geom.getCoordinate().y;
        } else {
            this.longitude = null;
            this.latitude = null;
        }
    }
}
