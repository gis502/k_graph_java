package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 10:09
 * @description: 灾损文件（专题图、报告）产出表
 */

@Data
@Builder
@TableName("assessment_output")
public class AssessmentOutput {

    @TableField(value = "id")
    private String id; // 编码
    @TableField(value = "eqqueue_id")
    private String eqqueueId; // 地震评估批次编码
    @TableField(value = "eqid")
    private String eqid; // 地震唯一标识符
    @TableField(value = "code")
    private String code; // 产品编码
    @TableField(value = "pro_time")
    private String proTime; // 产出时间
    @TableField(value = "file_type")
    private String fileType; // 文件类型
    @TableField(value = "file_name")
    private String fileName; // 文件名称
    @TableField(value = "file_extension")
    private String fileExtension; // 扩展名
    @TableField(value = "file_size")
    private Double fileSize; // 文件大小
    @TableField(value = "source_file")
    private String sourceFile; // 文件路径
    @TableField(value = "local_source_file")
    private String localSourceFile; // 本地文件路径
    @TableField(value = "remark")
    private String remark; // 备注
    @TableField(value = "size")
    private String size; // 专题图尺寸（A3）
    @TableField(value = "type")
    private String type; // 产出类型（1:专题图，2：报告）
    // 创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    // 修改时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE, value = "update_time")
    private LocalDateTime updateTime;
    @TableField(value = "is_deleted")
    private Integer isDeleted;

}
