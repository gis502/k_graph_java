package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 力量物资资金-大型、特种救援装备统计表(用户上传数据)
 */
@Data
@TableName(value = "large_special_rescue_equipment")
public class LargeSpecialRescueEquipment {
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
    @ExcelProperty({"大型、特种救援装备", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private LocalDateTime submissionDeadline;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"大型、特种救援装备", "震区名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String earthquakeAreaName;

    /**
     * 直升机
     */
    @TableField(value = "helicopter_count")
    @ExcelProperty({"大型、特种救援装备", "直升机"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer helicopterCount;

    /**
     * 舟桥
     */
    @TableField(value = "bridge_boat_count")
    @ExcelProperty({"大型、特种救援装备", "舟桥"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer bridgeBoatCount;

    /**
     * 翼龙无人机
     */
    @TableField(value = "wing_drone_count")
    @ExcelProperty({"大型、特种救援装备", "翼龙无人机"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private Integer wingDroneCount;
}