package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@TableName(value = "orthophotoimage")
public class OrthophotoImage {
    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 路径
     */
    @TableField(value = "path")
    private String path;

    /**
     * 高度
     */
    @TableField(value = "height")
    private String height;


    /**
     * 添加时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @TableField(value = "create_time") // 确保格式正确
    private LocalDateTime createTime; // 使用 Date 类型

    /**
     * id
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;

    /**
     * 旋转角度
     */
    @TableField(value = "angle")
    private BigDecimal angle;
    // 在保存前检查UUID是否为空，如果为空则自动生成
    public void generateUuidIfNotPresent() {
        if (this.uuid == null || this.uuid.trim().isEmpty()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }





}



