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
 * 震后生成-社会反应动态信息表(用户上传数据)
 */
@Data
@TableName(value = "public_opinion")
public class PublicOpinion {

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
  private String earthquakeName;

  /**
   * 震区（县/区）
   */
  @TableField(value = "earthquake_zone_name")
  @ExcelProperty({"社会反应动态信息", "震区（县/区）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private String earthquakeZoneName;


  /**
   * 统计截止时间
   */
  @TableField(value = "submission_deadline")
  @ExcelProperty({"社会反应动态信息", "统计截止时间"})
  @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private LocalDateTime submissionDeadline;

  /**
   * 宣传报道（篇）
   */
  @TableField(value = "publicity_report")
  @ExcelProperty({"社会反应动态信息", "宣传报道（篇）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long publicityReport;

  /**
   * 中省主要媒体报道（篇）
   */
  @TableField(value = "provincial_media_report")
  @ExcelProperty({"社会反应动态信息", "中省主要媒体报道（篇）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long provincialMediaReport;

  /**
   * 舆情风险提示（条）
   */
  @TableField(value = "public_opinion_risk_warning")
  @ExcelProperty({"社会反应动态信息", "舆情风险提示（条）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long publicOpinionRiskWarning;

  /**
   * 发布会（场）
   */
  @TableField(value = "press_conference")
  @ExcelProperty({"社会反应动态信息", "发布会（场）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long pressConference;

  /**
   * 处置负面舆论（条）
   */
  @TableField(value = "negative_opinion_disposal")
  @ExcelProperty({"社会反应动态信息", "处置负面舆论（条）"})
  @ColumnWidth(30)
  @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT)
  private long negativeOpinionDisposal;



  /**
   * 系统插入时间
   */
  @TableField(value = "system_insert_time")
  private LocalDateTime systemInsertTime;

  /**
   * 地震时间
   */
  @TableField(value = "earthquake_time")
  private LocalDateTime earthquakeTime;

}
