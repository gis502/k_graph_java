package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

@Data
@TableName(value = "seismic_intensity_circle")
public class SeismicIntensityCircle {
    @TableId(value = "circleid", type = IdType.NONE)
    private Object circleid;

    /**
     * 烈度范围
     */
    @TableField(value = "geom")

    private String geom;

    /**
     * 地震编号
     */
    @TableField(value = "eqid")
    private Object eqid;

    /**
     * 烈度
     */
    @TableField(value = "intensity")
    private Integer intensity;
}