package com.ruoyi.system.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
    * 震后生成-地震灾情信息-生命线震害信息-道路损毁表（用户上传数据）
    */
@Data
@TableName(value = "road_damage")
public class RoadDamage {
    /**
     * 序号
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
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "地震名称"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String earthquakeName;

    /**
     * 地震发生时间
     */
    @TableField(value = "earthquake_time")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "地震时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime earthquakeTime;

    /**
     * 震区名称
     */
    @TableField(value = "affected_area")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "震区（县/区）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String affectedArea;

    /**
     * 填报截止时间
     */
    @TableField(value = "reporting_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "统计截止时间"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Date reportingDeadline;


    /**
     * 高速公路及国道损毁情况
     */
    @TableField(value = "highways_national_roads")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "高速公路及国道"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String highwaysNationalRoads;

    /**
     * 省道损毁情况
     */
    @TableField(value = "provincial_roads")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "省道"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String provincialRoads;

    /**
     * 目前道路中断的村庄
     */
    @TableField(value = "villages_with_road_closures")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "目前道路中断村"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String villagesWithRoadClosures;

    /**
     * 正在抢修的道路
     */
    @TableField(value = "under_repair")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "正在抢修"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String underRepair;

    /**
     * 已恢复的道路
     */
    @TableField(value = "restored_roads")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "恢复道路"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private String restoredRoads;

    /**
     * 累计协调的运力，单位为车辆数
     */
    @TableField(value = "total_coordinated_transport_capacity")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "累计协调运力（车）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private Integer totalCoordinatedTransportCapacity;

    /**
     * 累计损毁的道路长度，单位为公里
     */
    @TableField(value = "total_road_damage_km")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "道路损毁（公里）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal totalRoadDamageKm;

    /**
     * 已抢通的道路长度，单位为公里
     */
    @TableField(value = "restored_km")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "已抢通（公里）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal restoredKm;

    /**
     * 待抢修的道路长度，单位为公里
     */
    @TableField(value = "pending_repair_km")
    @ExcelProperty({"交通电力通信-道路交通损毁及抢修情况统计表", "待抢修（公里）"})
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private BigDecimal pendingRepairKm;


    /**
     * 系统自动插入记录的时间
     */
    @TableField(value = "system_insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime systemInsertTime;



    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ColumnWidth(30)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.LEFT) // 设置为左对齐
    private LocalDateTime recordTime;


}
