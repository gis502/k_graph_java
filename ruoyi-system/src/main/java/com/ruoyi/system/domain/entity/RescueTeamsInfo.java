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

/**
 * 震后生成-应急处置信息-人员信息-救援队伍
 */
@Data
@TableName(value = "rescue_teams_info",autoResultMap = true)
public class RescueTeamsInfo {
    /**
     * 唯一标识
     */
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 所属机构
     */
    @TableField(value = "affiliated_agency")
    private String affiliatedAgency;

    /**
     * 级别名称
     */
    @TableField(value = "level_name")
    private String levelName;

    /**
     * 队伍类型名称
     */
    @TableField(value = "team_type_name")
    private String teamTypeName;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 总人数
     */
    @TableField(value = "total_members")
    private Integer totalMembers;

    /**
     * 成立时间
     */
    @TableField(value = "establishment_date")
    private String establishmentDate;

    /**
     * 主要职责
     */
    @TableField(value = "main_responsibilities")
    private String mainResponsibilities;

    /**
     * 专长描述
     */
    @TableField(value = "expertise_description")
    private String expertiseDescription;

    /**
     * 应急通讯方式
     */
    @TableField(value = "emergency_communication_methods")
    private String emergencyCommunicationMethods;

    /**
     * 预计准备时间
     */
    @TableField(value = "preparation_time")
    private Object preparationTime;

    /**
     * 集合出发地点
     */
    @TableField(value = "assembly_location")
    private String assemblyLocation;

    /**
     * 自备交通工具
     */
    @TableField(value = "self_transportation")
    private String selfTransportation;

    /**
     * 位置
     */
    @TableField(value = "geom")
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

    /**
     * 负责人
     */
    @TableField(value = "person_in_charge")
    private String personInCharge;

    /**
     * 负责人电话
     */
    @TableField(value = "charge_phone")
    private String chargePhone;

    /**
     * 密级名称
     */
    @TableField(value = "confidentiality_name")
    private String confidentialityName;

    /**
     * 修改人名称
     */
    @TableField(value = "modifier_name")
    private String modifierName;

    /**
     * 资质等级
     */
    @TableField(value = "qualification_level")
    private String qualificationLevel;

    /**
     * 数据来源
     */
    @TableField(value = "data_source")
    private String dataSource;

    /**
     * 备注
     */
    @TableField(value = "notes")
    private String notes;
}
