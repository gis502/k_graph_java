package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急指挥协调信息-会议（用户上传数据）
    */
@Data
@TableName(value = "meetings")
public class Meetings {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 地震标识
     */
    @TableField(value = "earthquake_id")
    private String earthquakeId;

    /**
     * 震区名称
     */
    @TableField(value = "earthquake_area_name")
    private String earthquakeAreaName;

    /**
     * 会议（场）
     */
    @TableField(value = "meeting_count")
    private Integer meetingCount;

    /**
     * 活动（场）
     */
    @TableField(value = "activity_count")
    private Integer activityCount;

    /**
     * 印发简报（份）
     */
    @TableField(value = "brief_report_count")
    private Integer briefReportCount;

    /**
     * 印发通知（份）
     */
    @TableField(value = "notice_count")
    private Integer noticeCount;

    /**
     * 会议纪要（份）
     */
    @TableField(value = "meeting_minutes_count")
    private Integer meetingMinutesCount;

    /**
     * 填报截止时间
     */
    @TableField(value = "report_deadline")
    private Date reportDeadline;

    /**
     * 系统插入时间
     */
    @TableField(value = "system_insert_time")
    private Date systemInsertTime;

    /**
     * 地震时间
     */
    @TableField(value = "earthquake_time")
    private Date earthquakeTime;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;
}