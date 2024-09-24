package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
    * 震后生成-标绘图标管理表
    */
@Data
@TableName(value = "plot_iconmanagement")
public class PlotIconmanagement {
    /**
     * 标识
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 名称
     */
    @TableField(value = "\"name\"")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "\"describe\"")
    private String describe;

    /**
     * 类型
     */
    @TableField(value = "\"type\"")
    private String type;

    /**
     * 标绘类型
     */
    @TableField(value = "plottype")
    private String plottype;

    /**
     * 图片
     */
    @TableField(value = "img")
    private String img;
}