package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-特殊工程修复信息-水利工程破坏类-堰塞湖
    */
@Data
@TableName(value = "dam_lake")
public class DamLake {
    /**
     * 序号
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 所在水系
     */
    @TableField(value = "river_system")
    private String riverSystem;

    /**
     * 堰塞体规模：顺河长度m
     */
    @TableField(value = "dam_scale_length")
    private String damScaleLength;

    /**
     * 堰塞体规模：顺河宽度m
     */
    @TableField(value = "dam_scale_width")
    private String damScaleWidth;

    /**
     * 堰塞体规模：顺河高度m
     */
    @TableField(value = "dam_scale_height")
    private String damScaleHeight;

    /**
     * 堰塞体危险性
     */
    @TableField(value = "estimated_danger")
    private String estimatedDanger;

    /**
     * 淹没和溃决影响区
     */
    @TableField(value = "area")
    private String area;

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
     * 预估库容
     */
    @TableField(value = "estimated_storage_capacity")
    private String estimatedStorageCapacity;
}