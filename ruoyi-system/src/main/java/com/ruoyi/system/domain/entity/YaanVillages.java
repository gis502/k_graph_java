package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;

@Data
@TableName(value = "yaan_villages")
public class YaanVillages {
    @TableId(value = "villages_id", type = IdType.NONE)
    private String villagesId;

    @TableField(value = "geom")
    private Geometry geom;

    @TableField(value = "villages_name")
    private String villagesName;

    @TableField(value = "seismic_fortification_intensity")
    private int seismicFortificationIntensity;
}
