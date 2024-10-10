package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-房屋信息-建筑物破坏类(轻微破坏建筑物、中等破坏建筑物、严重破坏建筑物、毁坏或倒塌建筑物)表
    */
@Data
@TableName(value = "building_damage_type")
public class BuildingDamageType {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 人员伤亡：无、有（10人及以下）、有（10人以上）
     */
    @TableField(value = "casualties")
    private String casualties;

    /**
     * 使用性质：住宅楼、自建房、办公楼、学校、医院、厂房、仓库、体育馆、展览馆、其它
     */
    @TableField(value = "usage_type")
    private String usageType;

    /**
     * 结构类型：砖木结构、砖混结构、钢筋混凝土结构、钢结构、其它
     */
    @TableField(value = "structure_type")
    private String structureType;

    /**
     * 破坏形式：振动破坏、地基失效破坏、次生效应破坏
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
    @TableField(value = "disposal_measures")
    private String disposalMeasures;

    /**
     * 先期处置阶段：暂未处置、正在处置、完成处置
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
