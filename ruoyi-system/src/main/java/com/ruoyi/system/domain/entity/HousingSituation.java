package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-房屋情况(用户上传数据)
    */
@Data
@TableName(value = "housing_situation")
public class HousingSituation {
    /**
     * 唯一标识
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震标识，标识地震事件的唯一标识符
     */
    @TableField(value = "earthquake_identifier")
    private String earthquakeIdentifier;

    /**
     * 震区名称，受影响地区的名称
     */
    @TableField(value = "affected_area_name")
    private String affectedAreaName;

    /**
     * 记录时间，记录房屋情况的时间
     */
    @TableField(value = "record_time")
    private Date recordTime;

    /**
     * 目前受损，受损房屋的数量或面积
     */
    @TableField(value = "currently_damaged")
    private Integer currentlyDamaged;

    /**
     * 目前禁用，无法使用的房屋数量或面积
     */
    @TableField(value = "currently_disabled")
    private Integer currentlyDisabled;

    /**
     * 目前限用，限制使用的房屋数量或面积
     */
    @TableField(value = "currently_restricted")
    private Integer currentlyRestricted;

    /**
     * 目前可用，可以使用的房屋数量或面积
     */
    @TableField(value = "currently_available")
    private Integer currentlyAvailable;

    /**
     * 填报截止时间，报告提交的最终期限
     */
    @TableField(value = "submission_deadline")
    private Date submissionDeadline;

    /**
     * 系统插入时间，记录被系统创建的时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 地震时间，地震发生的具体时间
     */
    @TableField(value = "earthquake_time")
    private Date earthquakeTime;

    /**
     * 地震名称，地震的描述性名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;

    /**
     * 房屋类别，房屋的类型或分类
     */
    @TableField(value = "housing_category")
    private String housingCategory;

    /**
     * 破坏等级，房屋破坏的程度
     */
    @TableField(value = "damage_grade")
    private String damageGrade;

    /**
     * 数量(以面积描述或一间数来描述)，受影响房屋的数量或面积
     */
    @TableField(value = "quantity_area")
    private String quantityArea;

    /**
     * 建筑年代，房屋建造的年份
     */
    @TableField(value = "construction_year")
    private String constructionYear;
}