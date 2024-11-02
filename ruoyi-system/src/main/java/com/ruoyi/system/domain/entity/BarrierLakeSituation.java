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
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@TableName(value = "barrier_lake_situation")
public class BarrierLakeSituation {


    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;
    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /*
     * 地震名称
     * */
    @TableField(value = "earthquake_name")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /*
     * 地震时间
     * */
    @TableField(value = "earthquake_time")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "地震时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime earthquakeTime;

    /**
     * 震区
     * */
    @TableField(value = "affected_area")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedArea;

    /*
     * 统计截止时间
     * */
    @TableField(value = "reporting_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "统计截止时间"})
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime reportingDeadline;


    /*
     * 堰塞湖情况
     * */
    @TableField(value = "barrier_lake")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "堰塞湖"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String barrierLake;

    /*
     * 受威胁地区
     * */
    @TableField(value = "threatened_areas")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "受威胁地区（乡镇、村）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String threatenedAreas;

    /*
     * 受威胁群众
     * */
    @TableField(value = "threatened_population")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "受威胁群众（户或人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer threatenedPopulation;

    /*
     * 避险转移
     * */
    @TableField(value = "evacuation")
    @ExcelProperty(value = {"堰塞湖（雍塞体）情况", "避险转移（户或人）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer evacuation;



    /**
     * 系统插入时间，记录被系统创建的时间
     */
    @TableField(value = "system_insert_time")
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime systemInsertTime;
    public LocalDateTime getSystemInsertTime() {
        return systemInsertTime != null ? systemInsertTime.truncatedTo(ChronoUnit.SECONDS) : null;
    }
}
