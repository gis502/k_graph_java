package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急处置信息-人员信息-救援行动类营救搜索区域类表
    */
@Data
@TableName(value = "rescue_action_search_area")
public class RescueActionSearchArea {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 营救区域位置
     */
    @TableField(value = "rescue_area_location")
    private String rescueAreaLocation;

    /**
     * 营救区域面积：km2
     */
    @TableField(value = "rescue_area_size_km2")
    private Double rescueAreaSizeKm2;

    /**
     * 营救队伍及人员数量
     */
    @TableField(value = "rescue_team_and_personnel_count")
    private String rescueTeamAndPersonnelCount;

    /**
     * 营救类型：综合救援、水域救援、航空救援、医疗救援、其他
     */
    @TableField(value = "rescue_type")
    private String rescueType;

    /**
     * 营救时间计划：月 日开始，预计 月 日结束
     */
    @TableField(value = "rescue_time_plan")
    private String rescueTimePlan;

    /**
     * 救援装备类型：破拆类（如液压剪、切割机等）、支撑类（如气垫、千斤顶等）、提升类（如吊机、担架等）
     */
    @TableField(value = "rescue_equipment_type")
    private String rescueEquipmentType;

    /**
     * 是否需要工程机械（如挖掘机、装载机、长臂吊车等）：是、否
     */
    @TableField(value = "need_engineering_machinery")
    private Boolean needEngineeringMachinery;

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
     * 记录时间
     */
    @TableField(value = "record_time")
    private Date recordTime;
}