package com.ruoyi.system.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;
@Data
@TableName(value = "risk_points")
public class RiskPoints {
    /**
     * 唯一标识
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private String uuid;


    /**
     * 隐患点地理位置描述
     */
    @TableField(value = "location")
    private String location;

    /**
     * 隐患点类型
     */
    @TableField(value = "risk_point_type")
    private String riskPointType;


    /**
     * 稳定性
     */
    @TableField(value = "stability")
    private String stability;


    /**
     * 险情等级
     */
    @TableField(value = "risk_level")
    private String riskLevel;

    /**
     * 诱发因素
     */
    @TableField(value = "inducing_factors")
    private String inducingFactors;


    /**
     * 与震中的距离
     */
    @TableField(exist = false)
    private Double distance;


    /**
     * 位置
     */
    @TableField(value = "geom")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geom;


    @TableField(exist = false) // Indicates no corresponding database column
    private Double longitude;

    @TableField(exist = false) // Indicates no corresponding database column
    private Double latitude;

    // 设置 geom 时提取经纬度
    public void setGeom(Geometry geom) {
        this.geom = geom;
        if (geom != null) {
            this.longitude = geom.getCoordinate().x;
            this.latitude = geom.getCoordinate().y;
        } else {
            this.longitude = null;
            this.latitude = null;
        }
    }

}
