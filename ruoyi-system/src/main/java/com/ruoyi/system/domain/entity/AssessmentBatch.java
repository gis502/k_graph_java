package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 9:51
 * @description: 灾损地震评估批次表
 */

@Data
@Builder
@TableName("assessment_batch")
public class AssessmentBatch {

    // 地震评估批次编码
    @TableField(value = "eqqueue_id")
    private String eqqueueId;
    // 地震唯一标识符
    @TableField(value = "eqid")
    private String eqid;
    // 评估批次
    @TableField(value = "batch")
    private Integer batch;
    // 启动类型（0自动,1手动）
    @TableField(value = "type")
    private String type;
    // 评估状态（0未开始，1正在计算，2正常完成，3人工停止，4异常结束,5超时结束）
    @TableField(value = "state")
    private Integer state;
    // 评估进度
    @TableField(value = "progress")
    private Double progress;
    // 备注
    @TableField(value = "remark")
    private String remark;
    // 逻辑删除状态（0正常，1删除）
    @TableField(value = "is_deleted")
    private Integer isDeleted;

}
