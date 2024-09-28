package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

/**
    * 震前准备-地震灾害和救灾背景信息-描述与灾区及其临区地震活动和地震成灾特性直接相关的各类自然与社会人文信息-应急避难场所
    */
@Data
@TableName(value = "emergency_shelters")
public class EmergencyShelters {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 唯一标识
     */
    @TableField(value = "unique_identifier")
    private String uniqueIdentifier;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 场所类型名称
     */
    @TableField(value = "shelter_type_name")
    private String shelterTypeName;

    /**
     * 面积
     */
    @TableField(value = "area")
    private BigDecimal area;

    /**
     * 可容纳人数
     */
    @TableField(value = "capacity")
    private Integer capacity;

    /**
     * 级别名称
     */
    @TableField(value = "level_name")
    private String levelName;

    /**
     * 密级
     */
    @TableField(value = "secret_level")
    private String secretLevel;

    /**
     * 行政区划
     */
    @TableField(value = "administrative_division")
    private String administrativeDivision;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 负责人
     */
    @TableField(value = "person_in_charge")
    private String personInCharge;

    /**
     * 应急电话
     */
    @TableField(value = "emergency_phone")
    private String emergencyPhone;

    /**
     * 应急手机
     */
    @TableField(value = "emergency_mobile")
    private String emergencyMobile;

    /**
     * 所属机构
     */
    @TableField(value = "affiliated_organization")
    private String affiliatedOrganization;

    /**
     * 投入使用时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 设计使用年限
     */
    @TableField(value = "design_service_life")
    private BigDecimal designServiceLife;

    /**
     * 基本情况
     */
    @TableField(value = "description")
    private String description;

    /**
     * 抗震设防烈度
     */
    @TableField(value = "seismic_intensity")
    private String seismicIntensity;

    /**
     * 数据来源单位
     */
    @TableField(value = "data_source_unit")
    private String dataSourceUnit;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    private String remarks;

    /**
     * 位置
     */
    @TableField(value = "geometry")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL) // 仅序列化非空字段
    private Geometry geom; // WKT 格式存储为 String

    @TableField(exist = false)
    private Double longitude;

    @TableField(exist = false)
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
