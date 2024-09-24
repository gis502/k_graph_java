package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-特殊工程修复信息-水利工程破坏类-基本完好堤防，中等破坏堤防，严重破坏堤防
    */
@Data
@TableName(value = "levee_damage")
public class LeveeDamage {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 所在水系
     */
    @TableField(value = "water_system")
    private String waterSystem;

    /**
     * 防洪标准：1级、2级、3级、4级、5级
     */
    @TableField(value = "flood_standard")
    private String floodStandard;

    /**
     * 建筑材料：土堤、石堤、土石混合堤、钢筋混凝土堤
     */
    @TableField(value = "construction_material")
    private String constructionMaterial;

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
}