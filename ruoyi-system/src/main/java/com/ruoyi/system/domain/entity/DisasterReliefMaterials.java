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
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 救灾物资情况统计表（累计）(用户上传数据)
 */
@Data
@TableName(value = "disaster_relief_supplies_accumulated")
public class DisasterReliefMaterials {
    /**
     * 序号，自增主键
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeAreaName;

    /**
     * 统计截止时间
     */
    @TableField(value = "submission_deadline")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Date submissionDeadline;

    /**
     * 当前救灾物资总数（万件）
     */
    @TableField(value = "current_disaster_supplies_total")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "当前救灾物资总数（万件）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer currentDisasterSuppliesTotal;

    /**
     * 调拨安置类物资（万件）
     */
    @TableField(value = "allocated_supplies_total")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "调拨安置类物资（万件）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer allocatedSuppliesTotal;

    /**
     * 帐篷（顶）
     */
    @TableField(value = "tents_count")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "帐篷（顶）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer tentsCount;

    /**
     * 棉被（床）
     */
    @TableField(value = "quilts_count")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "棉被（床）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer quiltsCount;

    /**
     * 折叠床（张）
     */
    @TableField(value = "folding_beds_count")
    @ExcelProperty({"力量物资资金-救灾物资情况（累计）统计表", "折叠床（张）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer foldingBedsCount;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;
}
