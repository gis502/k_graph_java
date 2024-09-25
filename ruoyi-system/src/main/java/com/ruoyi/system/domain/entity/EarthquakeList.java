package com.ruoyi.system.domain.entity;

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

import java.time.LocalDateTime;

/**
    * 震后生成-地震震情信息-地震列表
    */
@Data
@TableName(value = "earthquake_list")
public class EarthquakeList {
    /**
     * 唯一标识符
     */
    @TableId(value = "eqid", type = IdType.NONE)
    private Object eqid;

    /**
     * 地震名称
     */
    @TableField(value = "earthquake_name")
    private String earthquakeName;

    /**
     * 提供部门
     */
    @TableField(value = "providing_department")
    private String providingDepartment;

    /**
     * 地震发生地理位置
     */
    @TableField(value = "geom")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL) // 仅序列化非空字段
    private Geometry geom;

    @TableField(exist = false) // Indicates no corresponding database column
    private Double longitude;

    @TableField(exist = false) // Indicates no corresponding database column
    private Double latitude;

    /**
     * 发震时间
     */
    @TableField(value = "occurrence_time")
    private LocalDateTime occurrenceTime;

    /**
     * 震级
     */
    @TableField(value = "magnitude")
    private String magnitude;

    /**
     * 震源深度
     */
    @TableField(value = "\"depth\"")
    private String depth;

    /**
     * 地震烈度
     */
    @TableField(value = "intensity")
    private String intensity;

    /**
     * 震中名字
     */
    @TableField(value = "epicenter_name")
    private String epicenterName;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

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
