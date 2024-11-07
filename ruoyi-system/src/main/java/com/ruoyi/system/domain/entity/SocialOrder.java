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

/**
 * 震后生成-社会秩序信息表(用户上传数据)
 */
@Data
@TableName(value = "social_order")
public class SocialOrder {

  /**
   * 编号
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
  @ExcelProperty({"社会秩序", "地震名称"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private String earthquakeName;

  /**
   * 地震时间
   */
  @TableField(value = "earthquake_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ExcelProperty({"社会秩序", "地震时间"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private LocalDateTime earthquakeTime;

  /**
   * 震区名称
   */
  @TableField(value = "earthquake_area_name")
  @ExcelProperty({"社会秩序", "震区（县/区）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private String earthquakeAreaName;

  /**
   * 统计截止时间
   */
  @TableField(value = "reporting_deadline")
  @ExcelProperty({"社会秩序", "统计截止时间"})
  @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private LocalDateTime reportingDeadline;

  /**
   * 接报救助信息数量
   */
  @TableField(value = "reported_rescue_info")
  @ExcelProperty({"社会秩序", "接报救助信息（起）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long reportedRescueInfo;




  /**
   * 投入警力
   */
  @TableField(value = "police_force")
  @ExcelProperty({"社会秩序", "投入警力"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long policeForce;


  /**
   * 系统插入时间
   */
  @TableField(value = "system_insert_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime systemInsertTime;
}
