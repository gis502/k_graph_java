package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-次生/衍生灾害处置信息-衍生安全生产事故-火灾
    */
@Data
@TableName(value = "fire_disaster")
public class FireDisaster {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 企业（单位）名称
     */
    @TableField(value = "company_name")
    private String companyName;

    /**
     * 火灾类型
     */
    @TableField(value = "fire_type")
    private String fireType;

    /**
     * 火灾等级
     */
    @TableField(value = "fire_level")
    private String fireLevel;

    /**
     * 人员伤亡
     */
    @TableField(value = "casualties")
    private String casualties;

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
    @TableField(value = "initial_response_phase")
    private String initialResponsePhase;

    /**
     * 开始处置日期
     */
    @TableField(value = "start_date")
    private Date startDate;

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
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;
}
