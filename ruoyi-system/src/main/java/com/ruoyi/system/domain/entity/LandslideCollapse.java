package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-次生/衍生灾害处置信息-滑坡、崩塌表
    */
@Data
@TableName(value = "landslide_collapse")
public class LandslideCollapse {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 长度（m）
     */
    @TableField(value = "length_m")
    private BigDecimal lengthM;

    /**
     * 宽度（m）
     */
    @TableField(value = "width_m")
    private BigDecimal widthM;

    /**
     * 平均厚度（m）
     */
    @TableField(value = "average_thickness_m")
    private BigDecimal averageThicknessM;

    /**
     * 体积（m3）
     */
    @TableField(value = "volume_m3")
    private BigDecimal volumeM3;

    /**
     * 目前稳定状况：基本稳定、不稳定
     */
    @TableField(value = "current_stability_status")
    private String currentStabilityStatus;

    /**
     * 人员伤亡：无、有
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 威胁对象：地表建筑、交通线路、通讯电力设施、水库电站、管网工程、其他设施
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
