package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 倾斜摄影模型
    */
@Data
@TableName(value = "tiltphotographymodel")
public class Tiltphotographymodel {
    /**
     * 模型名称
     */
    @TableField(value = "\"name\"")
    private String name;

    /**
     * 模型存储路径
     */
    @TableField(value = "\"path\"")
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
    @TableField(value = "\"time\"")
    private String time;

    /**
     * 模型id
     */
    @TableField(value = "modelid")
    private String modelid;

    /**
     * 添加高程后，在z轴上旋转了多少度
     */
    @TableField(value = "rze")
    private Short rze;

    /**
     * 添加高程后，在z轴上的平移量
     */
    @TableField(value = "tze")
    private Short tze;
}
