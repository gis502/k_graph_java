package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 10:04
 * @description: 灾损地震影响场表
 */


@Data
@Builder
@TableName("assessment_intensity")
public class AssessmentIntensity {

    @TableField(value = "id")
    private String id; // 编码
    @TableField(value = "eqqueue_id")
    private String eqqueueId; // 地震评估批次编码
    @TableField(value = "eqid")
    private String eqId; // 地震唯一标识符
    @TableField(value = "batch")
    private String batch; // 计算批次
    @TableField(value = "file")
    private String file; // 文件路径
    @TableField(value = "local_file")
    private String localFile; // 本地文件路径
    @TableField(value = "file_type")
    private String fileType; // 文件类型
    @TableField(value = "is_deleted")
    private Integer isDeleted;

}
