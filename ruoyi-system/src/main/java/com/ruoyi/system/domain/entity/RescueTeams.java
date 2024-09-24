package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-灾害现场动态信息-组织机构运行能力恢复信息-救援部门，目标地点，队伍类型
    */
@Data
@TableName(value = "rescue_teams")
public class RescueTeams {
    /**
     * 唯一标识
     */
    @TableId(value = "unique_identifier", type = IdType.NONE)
    private String uniqueIdentifier;

    /**
     * 组织机构
     */
    @TableField(value = "organization")
    private String organization;

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
    @TableField(value = "total_people")
    private Integer totalPeople;

    /**
     * 成立日期
     */
    @TableField(value = "establishment_date")
    private Date establishmentDate;

    /**
     * 主要职责
     */
    @TableField(value = "main_responsibility")
    private String mainResponsibility;

    /**
     * 专业描述
     */
    @TableField(value = "professional_description")
    private String professionalDescription;

    /**
     * 应急联系方式
     */
    @TableField(value = "emergency_contact")
    private String emergencyContact;

    /**
     * 预估准备时间
     */
    @TableField(value = "estimated_preparation_time")
    private Date estimatedPreparationTime;

    /**
     * 集合出发地点
     */
    @TableField(value = "assembly_departure_location")
    private String assemblyDepartureLocation;

    /**
     * 自备交通工具
     */
    @TableField(value = "self_provided_transportation")
    private String selfProvidedTransportation;

    /**
     * 负责人
     */
    @TableField(value = "person_in_charge")
    private String personInCharge;

    /**
     * 负责人电话
     */
    @TableField(value = "person_in_charge_phone")
    private String personInChargePhone;

    /**
     * 保密级别
     */
    @TableField(value = "confidentiality_level")
    private String confidentialityLevel;

    /**
     * 修改人
     */
    @TableField(value = "modifier")
    private String modifier;

    /**
     * 资质级别
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

    @TableField(value = "geom")
    private Object geom;
}