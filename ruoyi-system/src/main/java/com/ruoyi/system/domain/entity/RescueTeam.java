package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
    * 震后生成-应急处置信息-人员信息-救援力量队伍类表
    */
@Data
@TableName(value = "rescue_team")
public class RescueTeam {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 队伍名称
     */
    @TableField(value = "team_name")
    private String teamName;

    /**
     * 队伍驻地：省
     */
    @TableField(value = "station_province")
    private String stationProvince;

    /**
     * 队伍驻地：市（州）
     */
    @TableField(value = "station_city")
    private String stationCity;

    /**
     * 队伍驻地：县（市、区）
     */
    @TableField(value = "station_county")
    private String stationCounty;

    /**
     * 人员数量
     */
    @TableField(value = "personnel_count")
    private Integer personnelCount;

    /**
     * 队伍性质：消防救援队伍、森林消防队伍、专业救援队伍、社会应急队伍、其他救援队伍（驻川解放军、武警部队和民兵等）
     */
    @TableField(value = "team_nature")
    private String teamNature;

    /**
     * 队伍类型：综合救援类、工程抢险类、水域救援类、排涝抢险类、航空救援类、应急通信类、电力抢险类、医疗救援类、后勤保障类
     */
    @TableField(value = "team_type")
    private String teamType;

    /**
     * 出发日期
     */
    @TableField(value = "departure_date")
    private Date departureDate;

    /**
     * 预计到达日期
     */
    @TableField(value = "estimated_arrival_date")
    private Date estimatedArrivalDate;

    /**
     * 拟抢险救援区域
     */
    @TableField(value = "planned_rescue_area")
    private String plannedRescueArea;

    /**
     * 联系人员
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 是否待命
     */
    @TableField(value = "on_standby")
    private String onStandby;

    /**
     * 记录时间
     */
    @TableField(value = "record_time")
    private Date recordTime;

    /**
     * 地震id
     */
    @TableField(value = "eqid")
    private String eqid;

    @TableField(value="describe_things")
    private String describeThings;
}
