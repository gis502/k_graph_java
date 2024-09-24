package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-次生/衍生灾害处置信息-泥石流
    */
@Data
@TableName(value = "debris_flow")
public class DebrisFlow {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 河沟坡度
     */
    @TableField(value = "river_slope")
    private BigDecimal riverSlope;

    /**
     * 植被覆盖率
     */
    @TableField(value = "vegetation_coverage")
    private String vegetationCoverage;

    /**
     * 流速
     */
    @TableField(value = "flow_speed")
    private BigDecimal flowSpeed;

    /**
     * 面积 (m2)
     */
    @TableField(value = "area")
    private BigDecimal area;

    /**
     * 平均厚度 (m)
     */
    @TableField(value = "average_thickness")
    private BigDecimal averageThickness;

    /**
     * 物质组成
     */
    @TableField(value = "material_composition")
    private String materialComposition;

    /**
     * 目前稳定状况
     */
    @TableField(value = "current_stability_status")
    private String currentStabilityStatus;

    /**
     * 人员伤亡
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 威胁对象
     */
    @TableField(value = "threatened_objects")
    private String threatenedObjects;

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
     * 开始处置时间
     */
    @TableField(value = "start_disposal_time")
    private Date startDisposalTime;

    /**
     * 预计处置完成时间
     */
    @TableField(value = "estimated_completion_time")
    private Date estimatedCompletionTime;

    /**
     * 实际处置完成时间
     */
    @TableField(value = "actual_completion_time")
    private Date actualCompletionTime;

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