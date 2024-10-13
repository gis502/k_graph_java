package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急处置信息-人员信息-救援行动类_伤亡人员表
    */
@Data
@TableName(value = "rescue_action_casualties")
public class RescueActionCasualties {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 所在位置
     */
    @TableField(value = "\"location\"")
    private String location;

    /**
     * 人员伤亡状态：轻伤、重伤、危重伤、死亡
     */
    @TableField(value = "casualty_status")
    private String casualtyStatus;

    /**
     * ABCD评分：窒息与呼吸困难、出血与失血性休克、昏迷与颅脑外伤、正在发生的突然死亡
     */
    @TableField(value = "abcd_score")
    private String abcdScore;

    /**
     * 新增人数：轻伤
     */
//    @TableField(value = "new_mild_injury_count")
//    private Integer newMildInjuryCount;
//
//    /**
//     * 新增人数：重伤
//     */
//    @TableField(value = "new_serious_injury_count")
//    private Integer newSeriousInjuryCount;
//
//    /**
//     * 新增人数：危重伤
//     */
//    @TableField(value = "new_critical_injury_count")
//    private Integer newCriticalInjuryCount;
//
//    /**
//     * 新增人数：死亡
//     */
//    @TableField(value = "new_death_count")
//    private Integer newDeathCount;

    /**
     * 累计人数：轻伤
     */
    @TableField(value = "total_mild_injury_count")
    private Integer totalMildInjuryCount;

    /**
     * 累计人数：重伤
     */
    @TableField(value = "total_serious_injury_count")
    private Integer totalSeriousInjuryCount;

    /**
     * 累计人数：危重伤
     */
    @TableField(value = "total_critical_injury_count")
    private Integer totalCriticalInjuryCount;

    /**
     * 累计人数：死亡
     */
    @TableField(value = "total_death_count")
    private Integer totalDeathCount;

    /**
     * 医疗救援队伍
     */
    @TableField(value = "medical_rescue_team")
    private String medicalRescueTeam;

    /**
     * 联系人员
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    private Date recordTime;

    @TableField(value = "new_count")
    private Integer newCount;

    @TableField(value = "total_count")
    private Integer totalCount;

    /**
     * 标绘id
     */
    @TableField(value = "plot_id")
    private String plotId;

    @TableField(value = "eqid")
    private String eqid;
}
