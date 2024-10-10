package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-次生/衍生灾害处置信息-地面沉降,地面塌陷
    */
@Data
@TableName(value = "ground_settlement_sinkhole")
public class GroundSettlementSinkhole {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 沉降面积or影响范围面积m2
     */
    @TableField(value = "affected_area")
    private BigDecimal affectedArea;

    /**
     * 累计沉降量（h）m
     */
    @TableField(value = "cumulative_settlement")
    private BigDecimal cumulativeSettlement;

    /**
     * 发展变化
     */
    @TableField(value = "development_change")
    private String developmentChange;

    /**
     * 人员伤亡
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 威胁对象
     */
    @TableField(value = "threatened_target")
    private String threatenedTarget;

    /**
     * 处置队伍
     */
    @TableField(value = "response_team")
    private String responseTeam;

    /**
     * 处置措施
     */
    @TableField(value = "response_measures")
    private String responseMeasures;

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
    @TableField(value = "expected_completion_date")
    private Date expectedCompletionDate;

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
     * 图片
     */
    @TableField(value = "image")
    private Object image;

    @TableField(value = "geom")
    private Object geom;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
