package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急指挥协调信息-应急响应信息表
    */
@Data
@TableName(value = "emergency_response_info")
public class EmergencyResponseInfo {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 时间
     */
    @TableField(value = "response_time")
    private Date responseTime;

    /**
     * 单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 等级
     */
    @TableField(value = "\"level\"")
    private String level;

    /**
     * 状态：启动，升级，结束
     */
    @TableField(value = "\"status\"")
    private String status;

    /**
     * 地震标识
     */
    @TableField(value = "eqid")
    private String eqid;
}