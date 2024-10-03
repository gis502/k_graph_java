package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "yaan_json")
public class YaanJson {
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    @TableField(value = "geom")
    private Object geom;

    @TableField(value = "adcode")
    private Integer adcode;

    @TableField(value = "\"name\"")
    private String name;

    @TableField(value = "center")
    private Integer center;

    @TableField(value = "centroid")
    private Integer centroid;

    @TableField(value = "childrennu")
    private Integer childrennu;

    @TableField(value = "\"level\"")
    private String level;

    @TableField(value = "parent")
    private Integer parent;

    @TableField(value = "subfeature")
    private Integer subfeature;

    @TableField(value = "acroutes")
    private Integer acroutes;
}