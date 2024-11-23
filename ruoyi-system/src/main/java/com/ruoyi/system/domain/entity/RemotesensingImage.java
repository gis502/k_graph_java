package com.ruoyi.system.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@TableName(value = "remotesensingimage")
public class RemotesensingImage {

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



    /**
     * 拍摄时间
     */
    @TableField(value = "shooting_time")
    private LocalDateTime shootingTime;

}
