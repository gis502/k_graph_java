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

@Data
@TableName(value = "work_group_log")
public class WorkGroupLog {

    /**
     * 序号，自增主键
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 地震标识，标识地震事件的唯一标识符
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 地震名称，地震的描述性名称
     */
    @TableField(value = "earthquake_name")
    @ExcelProperty({"工作组日志动态", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震时间，地震发生的具体时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"工作组日志动态", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区，受影响的地区名称
     */
    @TableField(value = "earthquake_area_name")
    @ExcelProperty(value = {"工作组日志动态", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeAreaName;

    /**
     * 填报截至时间，报告提交的最终期限
     */
    @TableField(value = "submission_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = {"工作组日志动态", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime submissionDeadline;

    @TableField(value = "work_group")
    @ExcelProperty(value = {"工作组日志动态", "工作组"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String workGroup;

    @TableField(value = "report_department")
    @ExcelProperty(value = {"工作组日志动态", "报告部门"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String reportDepartment;

    @TableField(value = "work_status")
    @ExcelProperty(value = {"工作组日志动态", "今日工作开展情况"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String workStatus;

    @TableField(value = "work_issues")
    @ExcelProperty(value = {"工作组日志动态", "困难及问题清单"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String workIssues;

    @TableField(value = "requirement_list")
    @ExcelProperty(value = {"工作组日志动态", "需求清单"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String requirementList;

    private LocalDateTime recordTime;


}
