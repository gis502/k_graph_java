package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.system.domain.handler.PgGeometryTypeHandler;
import lombok.Data;

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
    @TableField(value = "geom",typeHandler = PgGeometryTypeHandler.class)
    private String geom; // WKT 格式存储为 String

    @TableField(exist = false)
    private Double longitude;

    @TableField(exist = false)
    private Double latitude;

    // Getter 和 Setter 方法
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void parseGeom() {
//        System.out.println("初始状态: " + this.geom);
        if (this.geom != null && this.geom.startsWith("POINT(") && this.geom.endsWith(")")) {
            try {
                // 去掉 "POINT(" 和 ")"，然后按空格拆分字符串
                String[] coordinates = this.geom.substring(6, this.geom.length() - 1).split(" ");
                this.longitude = Double.parseDouble(coordinates[0]);
                this.latitude = Double.parseDouble(coordinates[1]);
            } catch (Exception e) {
                System.err.println("Error parsing geom: " + e.getMessage() + " for geom: " + this.geom);
            }
        } else {
            System.err.println("Invalid geom format or geom is null: " + this.geom);
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
