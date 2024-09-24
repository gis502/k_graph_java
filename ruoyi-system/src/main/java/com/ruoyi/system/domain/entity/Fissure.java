package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-次生/衍生灾害处置信息-地裂缝
    */
@Data
@TableName(value = "fissure")
public class Fissure {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 长度 (m)
     */
    @TableField(value = "\"length\"")
    private BigDecimal length;

    /**
     * 宽度 (m)
     */
    @TableField(value = "width")
    private BigDecimal width;

    /**
     * 深度 (m)
     */
    @TableField(value = "\"depth\"")
    private BigDecimal depth;

    /**
     * 活动性
     */
    @TableField(value = "activity_status")
    private String activityStatus;

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
}