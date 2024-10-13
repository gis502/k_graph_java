package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-特殊工程修复信息-水利工程破坏类-基本完好大坝,中等破坏大坝,严重破坏大坝
    */
@Data
@TableName(value = "dam_condition")
public class DamCondition {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 水库（电站）名称
     */
    @TableField(value = "reservoir_name")
    private String reservoirName;

    /**
     * 大坝类型
     */
    @TableField(value = "dam_type")
    private String damType;

    /**
     * 工程规模
     */
    @TableField(value = "project_scale")
    private String projectScale;

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
     * 先期处置阶段
     */
    @TableField(value = "initial_disposal_phase")
    private String initialDisposalPhase;

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
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
