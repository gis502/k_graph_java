package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * 倾斜摄影模型
 */
@Data
@TableName(value = "tiltphotographymodel")
public class Tiltphotographymodel {
    /**
     * 模型名称
     */
    @TableField(value = "name") // 修改为不使用双引号
    private String name;

    /**
     * 模型存储路径
     */
    @TableField(value = "path") // 修改为不使用双引号
    private String path;

    /**
     * 在z轴上旋转了多少度
     */
    @TableField(value = "rz")
    private Integer rz;

    /**
     * 在z轴上的平移量
     */
    @TableField(value = "tz")
    private Integer tz;

    /**
     * 模型添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "time") // 确保格式正确
    private Date time; // 使用 Date 类型

    /**
     * 模型id
     */
    @TableId(value = "uuid", type = IdType.NONE) // 使用 NONE 类型，手动生成 UUID
    private String uuid;

    /**
     * 添加高程后，在z轴上旋转了多少度
     */
    @TableField(value = "rze")
    private Double rze;

    /**
     * 添加高程后，在z轴上的平移量
     */
    @TableField(value = "tze")
    private Double tze;

    /**
     * 模型大小（GB）
     */
    @TableField(value = "model_size")
    private Double modelSize;

    // 在保存前检查 UUID 是否为空，如果为空则自动生成
    public void generateUuidIfNotPresent() {
        if (this.uuid == null || this.uuid.trim().isEmpty()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
