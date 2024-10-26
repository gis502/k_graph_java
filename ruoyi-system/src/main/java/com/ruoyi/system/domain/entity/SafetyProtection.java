package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName(value = "safety_protection")
public class SafetyProtection {
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    @TableField(value = "application_type")
    private String applicationType;

    @TableField(value = "source")
    private String source;

    @TableField(value = "agreement")
    private String agreement;

    @TableField(value = "port")
    private Integer port;

    @TableField(value = "notes")
    private String notes;

    @TableField(value = "tactics")
    private String tactics;

    @TableField(exist = false)
    private String ifDelete;
}
