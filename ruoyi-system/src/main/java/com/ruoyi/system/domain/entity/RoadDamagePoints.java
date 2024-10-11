package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-生命线修复信息-交通设施破坏类-公路破坏点
    */
@Data
@TableName(value = "road_damage_points")
public class RoadDamagePoints {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 公路名称
     */
    @TableField(value = "road_name")
    private String roadName;

    /**
     * 公路等级：高速、国道、省道、县道、乡道、其它
     */
    @TableField(value = "road_level")
    private String roadLevel;

    /**
     * 人员伤亡：无、有（10人及以下）、有（10人以上）
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 破坏形式：路面破坏（如路面出现裂缝、悬空、断裂等）、路基破坏（如路基隆起、沉陷等）、边坡破坏（如震后滑坡、崩塌等）
     */
    @TableField(value = "damage_type")
    private String damageType;

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
