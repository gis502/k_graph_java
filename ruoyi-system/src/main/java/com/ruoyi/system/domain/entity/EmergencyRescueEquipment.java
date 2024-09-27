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
 * 震前准备-救灾能力储备信息-抢险救援装备填报表
 */
@Data
@TableName(value = "emergency_rescue_equipment")
public class EmergencyRescueEquipment {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 县（区）
     */
    @TableField(value = "county")
    private String county;

    /**
     * 合计总件套数
     */
    @TableField(value = "total_items")
    private Integer totalItems;

    /**
     * 红外探测仪
     */
    @TableField(value = "infrared_detector")
    private Integer infraredDetector;

    /**
     * 光学探测仪(蛇眼)
     */
    @TableField(value = "optical_detector")
    private Integer opticalDetector;

    /**
     * 液压扩张钳
     */
    @TableField(value = "hydraulic_expansion_pliers")
    private Integer hydraulicExpansionPliers;

    /**
     * 液压剪切钳
     */
    @TableField(value = "hydraulic_cutting_pliers")
    private Integer hydraulicCuttingPliers;

    /**
     * 凿岩机
     */
    @TableField(value = "rock_drill")
    private Integer rockDrill;

    /**
     * 撬棍（把）
     */
    @TableField(value = "pry_bar")
    private Integer pryBar;

    /**
     * 钢筋速断器
     */
    @TableField(value = "rebar_cutter")
    private Integer rebarCutter;

    /**
     * 手动液压千斤顶
     */
    @TableField(value = "manual_hydraulic_jack")
    private Integer manualHydraulicJack;

    /**
     * 发光棒
     */
    @TableField(value = "glow_stick")
    private Integer glowStick;

    /**
     * 油料（升）
     */
    @TableField(value = "fuel")
    private BigDecimal fuel;

    /**
     * 抗拉索
     */
    @TableField(value = "tension_wire")
    private Integer tensionWire;

    /**
     * 救援绳（米）
     */
    @TableField(value = "rescue_rope")
    private BigDecimal rescueRope;

    /**
     * 抛绳器
     */
    @TableField(value = "rope_thrower")
    private Integer ropeThrower;

    /**
     * 折叠梯（个）
     */
    @TableField(value = "folding_ladder")
    private Integer foldingLadder;

    /**
     * 锹/镐/钩/叉/锤
     */
    @TableField(value = "shovel_pick_hook_fork_hammer")
    private Integer shovelPickHookForkHammer;

    /**
     * 折叠铲（把）
     */
    @TableField(value = "folding_shovel")
    private Integer foldingShovel;

    /**
     * 口哨（个）
     */
    @TableField(value = "whistle")
    private Integer whistle;

    /**
     * 头盔（顶）
     */
    @TableField(value = "helmet")
    private Integer helmet;

    /**
     * 雨鞋（双）
     */
    @TableField(value = "rain_boots")
    private Integer rainBoots;

    /**
     * 手套（双）
     */
    @TableField(value = "gloves")
    private Integer gloves;

    /**
     * 救生缆索（米）
     */
    @TableField(value = "lifeline")
    private BigDecimal lifeline;

    /**
     * 排水泵（台）
     */
    @TableField(value = "drainage_pump")
    private Integer drainagePump;

    /**
     * 风力灭火机（个）
     */
    @TableField(value = "wind_fire_extinguisher")
    private Integer windFireExtinguisher;

    /**
     * 铁锹（把）
     */
    @TableField(value = "shovel")
    private Integer shovel;

    /**
     * 救生衣（件）
     */
    @TableField(value = "life_jacket")
    private Integer lifeJacket;

    /**
     * 救生圈（个）
     */
    @TableField(value = "lifebuoy")
    private Integer lifebuoy;

    /**
     * 警示带（米）
     */
    @TableField(value = "warning_tape")
    private BigDecimal warningTape;

    /**
     * 对讲机（台）
     */
    @TableField(value = "walkie_talkie")
    private Integer walkieTalkie;

    /**
     * 扩音器（个）
     */
    @TableField(value = "megaphone")
    private Integer megaphone;

    /**
     * 锣（个）
     */
    @TableField(value = "gong")
    private Integer gong;

    /**
     * 头灯（个）
     */
    @TableField(value = "headlamp")
    private Integer headlamp;

    /**
     * 手提照明灯（个）
     */
    @TableField(value = "portable_light")
    private Integer portableLight;

    /**
     * 医疗急救箱
     */
    @TableField(value = "first_aid_kit")
    private Integer firstAidKit;

    /**
     * 挖掘机
     */
    @TableField(value = "excavator")
    private Integer excavator;

    /**
     * 装载机（推土机）
     */
    @TableField(value = "loader")
    private Integer loader;

    /**
     * 抽水泵
     */
    @TableField(value = "water_pump")
    private Integer waterPump;

    /**
     * 接力水泵
     */
    @TableField(value = "relay_water_pump")
    private Integer relayWaterPump;

    /**
     * 移动水囊（个）
     */
    @TableField(value = "mobile_water_bag")
    private Integer mobileWaterBag;

    /**
     * 背负式喷水灭火抢
     */
    @TableField(value = "backpack_sprayer")
    private Integer backpackSprayer;

    /**
     * 油锯（个）
     */
    @TableField(value = "chainsaw")
    private Integer chainsaw;

    /**
     * 水带（米）
     */
    @TableField(value = "hose")
    private BigDecimal hose;

    /**
     * 消防水车
     */
    @TableField(value = "fire_truck")
    private Integer fireTruck;

    /**
     * 其他
     */
    @TableField(value = "other")
    private String other;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

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
     * 联系人
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 插入时间
     */
    @TableField(value = "insertion_time")
    private Date insertionTime;
}
