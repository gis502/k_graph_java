package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
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
@TableName(value = "rescue_force_situation")
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
    private String earthquakeName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    private LocalDateTime earthquakeTime;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private LocalDateTime systemInsertTime;

    /**
     * 统计截止时间
     */
    @TableField(value = "submission_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"救灾物资情况（累计）", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Date submissionDeadline;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"救灾物资情况（累计）", "震区名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeAreaName;

    /**
     * 当前救灾物资总数（万件）
     */
    @TableField(value = "current_disaster_supplies_total")
    @ExcelProperty({"救灾物资情况（累计）", "当前救灾物资总数（万件）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer currentDisasterSuppliesTotal;

    /**
     * 调拨安置类物资（万件）
     */
    @TableField(value = "allocated_supplies_total")
    @ExcelProperty({"救援力量情况表", "调拨安置类物资（万件）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer allocatedSuppliesTotal;

    /**
     * 帐篷（顶）
     */
    @TableField(value = "tents_count")
    @ExcelProperty({"救灾物资情况（累计）", "帐篷（顶）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer tentsCount;

    /**
     * 棉被（床）
     */
    @TableField(value = "quilts_count")
    @ExcelProperty({"救灾物资情况（累计）", "棉被（床）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer quiltsCount;

    /**
     * 折叠床（张）
     */
    @TableField(value = "folding_beds_count")
    @ExcelProperty({"救灾物资情况（累计）", "折叠床（张）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer foldingBedsCount;
}
