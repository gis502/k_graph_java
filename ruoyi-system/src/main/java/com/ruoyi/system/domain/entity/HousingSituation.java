package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.sf.cglib.core.Local;

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
    private String uuid;

    /**
     * 地震标识，标识地震事件的唯一标识符
     */
    @TableField(value = "earthquake_identifier")
    private String earthquakeIdentifier;

    /**
     * 地震名称，地震的描述性名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"房屋情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震时间，地震发生的具体时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty({"房屋情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称，受影响地区的名称
     */
    @TableField(value = "affected_area_name")
    @ExcelProperty({"房屋情况", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedAreaName;


    /**
     * 填报截止时间，报告提交的最终期限
     */
    @TableField(value = "submission_deadline")
    @ExcelProperty({"房屋情况", "统计截止时间"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐 @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDeadline;

    /**
     * 目前受损，受损房屋的数量或面积
     */
    @TableField(value = "currently_damaged")
    @ExcelProperty({"房屋情况", "目前受损"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentlyDamaged;

    /**
     * 目前禁用，无法使用的房屋数量或面积
     */
    @TableField(value = "currently_disabled")
    @ExcelProperty({"房屋情况", "目前禁用"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentlyDisabled;

    /**
     * 目前限用，限制使用的房屋数量或面积
     */
    @TableField(value = "currently_restricted")
    @ExcelProperty({"房屋情况", "目前限用"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentlyRestricted;

    /**
     * 目前可用，可以使用的房屋数量或面积
     */
    @TableField(value = "currently_available")
    @ExcelProperty({"房屋情况", "目前可用"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer currentlyAvailable;


    /**
     * 系统插入时间，记录被系统创建的时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 记录时间，记录房屋情况的时间
     */
    @TableField(value = "record_time")
    private Date recordTime;

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
