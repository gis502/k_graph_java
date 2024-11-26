package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 10:17
 * @description: 灾损地震数据（人员伤亡、经济损失、建筑破坏）表
 */

@Data
@TableName("assessment_result")
public class AssessmentResult {

    @TableField("id")
    private String id; // 编码

    @TableField("eqqueue_id")
    private String eqqueueId; // 地震评估批次编码

    @TableField("eqid")
    private String eqid; // 地震唯一标识符

    @TableField("batch")
    private String batch; // 计算批次

    @TableField("eq_name")
    private String eqName; // 地震名称

    @TableField("inty")
    private String inty; // 烈度值

    @TableField("pac")
    private String pac; // 乡镇代码

    @TableField("pac_name")
    private String pacName; // 乡镇名称

    @TableField("building_damage")
    private String buildingDamage; // 建筑破坏面积（万平方米）

    @TableField("pop")
    private String pop; // 受灾人数(人)

    @TableField("death")
    private String death; // 死亡人数（人）

    @TableField("missing")
    private String missing; // 失踪人数（人）

    @TableField("injury")
    private String injury; // 受伤人数（人）

    @TableField("buried_count")
    private String buriedCount; // 压埋人数（人）

    @TableField("reset_number")
    private String resetNumber; // 需紧急安置人员（人）

    @TableField("economic_loss")
    private String economicLoss; // 经济损失（万元）


}
