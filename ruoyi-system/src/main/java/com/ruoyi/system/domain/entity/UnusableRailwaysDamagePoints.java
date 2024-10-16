package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-交通设施破坏类-不可通行铁路，铁路破坏点
    */
@Data
@TableName(value = "unusable_railways_damage_points")
public class UnusableRailwaysDamagePoints {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 铁路名称
     */
    @TableField(value = "railway_name")
    private String railwayName;

    /**
     * 人员伤亡：无、有（10人及以下）、有（10人以上）
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 铁路等级：客运专线、客货共线、货运专线
     */
    @TableField(value = "railway_level")
    private String railwayLevel;

    /**
     * 处置队伍
     */
    @TableField(value = "disposal_team")
    private String disposalTeam;

    /**
     * 处置措施
     */
    @TableField(value = "disposal_measures")
    private String disposalMeasures;

    /**
     * 先期处置阶段：暂未处置、正在处置、完成处置
     */
    @TableField(value = "initial_disposal_phase")
    private String initialDisposalPhase;

    /**
     * 先期处置过程：月日开始处置
     */
    @TableField(value = "initial_disposal_start_date")
    private Date initialDisposalStartDate;

    /**
     * 预计月日完成处置
     */
    @TableField(value = "initial_disposal_estimated_end_date")
    private Date initialDisposalEstimatedEndDate;

    /**
     * 实际月日完成处置
     */
    @TableField(value = "initial_disposal_actual_end_date")
    private Date initialDisposalActualEndDate;

    /**
     * 联系人员
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
