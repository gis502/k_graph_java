package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 震后生成-应急指挥协调信息-会议（用户上传数据）
 */
@Data
@TableName(value = "meetings")
public class Meetings {
    @TableId(value = "uuid", type = IdType.NONE)
    @ExcelIgnore
    private String uuid;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    @ExcelProperty({"会议", "地震标识"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeId;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty({"会议", "震区名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeAreaName;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"会议", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Date earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty(value = {"会议", "地震名称"})
    @ColumnWidth(30)
    private String earthquakeName;
    /**
     * 填报截止时间
     */
    @TableField(value = "report_deadline")
    @ExcelProperty(value = {"会议", "填报截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Date reportDeadline;

    /**
     * 会议（场）
     */
    @TableField(value = "meeting_count")
    @ExcelProperty(value = {"会议", "会议（场）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer meetingCount;

    /**
     * 活动（场）
     */
    @TableField(value = "activity_count")
    @ExcelProperty(value = {"会议", "活动（场）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer activityCount;

    /**
     * 印发简报（份）
     */
    @TableField(value = "brief_report_count")
    @ExcelProperty(value = {"会议", "印发简报（份）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer briefReportCount;

    /**
     * 印发通知（份）
     */
    @TableField(value = "notice_count")
    @ExcelProperty(value = {"会议", "印发通知（份）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer noticeCount;

    /**
     * 会议纪要（份）
     */
    @TableField(value = "meeting_minutes_count")
    @ExcelProperty(value = {"会议", "会议纪要（份）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer meetingMinutesCount;



    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelIgnore
    private Date systemInsertTime;
}
