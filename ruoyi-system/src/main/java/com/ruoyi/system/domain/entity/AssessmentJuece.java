package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "assessment_juece")
public class AssessmentJuece {
    @TableId(value = "id", type = IdType.NONE)
    private Object id;

    @TableField(value = "eqid")
    private String eqid;

    @TableField(value = "eqqueue_id")
    private String eqqueueId;

    /**
     * 产出时间
     */
    @TableField(value = "pro_time")
    private LocalDateTime proTime;

    /**
     * 路径
     */
    @TableField(value = "source_file")
    private String sourceFile;

    @TableField(value = "file_name")
    private String fileName;
}
