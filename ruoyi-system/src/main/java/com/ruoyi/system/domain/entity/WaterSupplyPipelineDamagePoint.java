package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-生命线工程破坏类-供水管线破坏点
    */
@Data
@TableName(value = "water_supply_pipeline_damage_point")
public class WaterSupplyPipelineDamagePoint {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 破坏形式
     */
    @TableField(value = "damage_form")
    private String damageForm;

    /**
     * 处置队伍
     */
    @TableField(value = "disposal_team")
    private String disposalTeam;

    /**
     * 处置措施
     */
    @TableField(value = "disposal_measure")
    private String disposalMeasure;

    /**
     * 先期处置阶段
     */
    @TableField(value = "initial_disposal_stage")
    private String initialDisposalStage;

    /**
     * 开始处置日期
     */
    @TableField(value = "start_disposal_date")
    private Date startDisposalDate;

    /**
     * 预计完成处置日期
     */
    @TableField(value = "estimated_completion_date")
    private Date estimatedCompletionDate;

    /**
     * 实际完成处置日期
     */
    @TableField(value = "actual_completion_date")
    private Date actualCompletionDate;

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
     * 位置，PostGIS类型
     */
    @TableField(value = "\"location\"")
    private Object location;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
