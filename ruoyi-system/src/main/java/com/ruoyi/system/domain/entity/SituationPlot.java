package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-态势标绘表
    */
@Data
@TableName(value = "situation_plot")
public class SituationPlot {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震ID
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 标绘ID
     */
    @TableField(value = "plot_id")
    private String plotId;

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
    private Date creationTime;

    /**
     * 高程
     */
    @TableField(value = "elevation")
    private BigDecimal elevation;

    /**
     * 标绘结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 是否删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 位置
     */
    @TableField(value = "geom")
    private Object geom;
}