package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.ruoyi.system.domain.handler.PgGeometryTypeHandler;
import lombok.Data;

/**
 * 震前准备-救灾能力储备信息-生活类救灾物资储备情况统计表
 */
@Data
@TableName(value = "disaster_relief_supplies",autoResultMap = true)
public class DisasterReliefSupplies {
    @TableId(value = "uuid", type = IdType.NONE)
    private Object uuid;

    /**
     * 县（区）
     */
    @TableField(value = "county")
    private String county;

    /**
     * 储备库点数量（个）
     */
    @TableField(value = "storage_points_count")
    private Integer storagePointsCount;

    /**
     * 合计总件套数
     */
    @TableField(value = "total_items_count")
    private Integer totalItemsCount;

    /**
     * 救灾帐篷（顶）
     */
    @TableField(value = "tents")
    private Integer tents;

    /**
     * 棉被（床）
     */
    @TableField(value = "quilts")
    private Integer quilts;

    /**
     * 其他被子（床）
     */
    @TableField(value = "other_blankets")
    private Integer otherBlankets;

    /**
     * 棉衣裤（套）
     */
    @TableField(value = "cotton_clothing")
    private Integer cottonClothing;

    /**
     * 棉大衣（件）
     */
    @TableField(value = "cotton_coats")
    private Integer cottonCoats;

    /**
     * 其他衣物（套、件）
     */
    @TableField(value = "other_clothing")
    private Integer otherClothing;

    /**
     * 毛毯（床）
     */
    @TableField(value = "blankets")
    private Integer blankets;

    /**
     * 折叠床（张）
     */
    @TableField(value = "folding_beds")
    private Integer foldingBeds;

    /**
     * 高低床（套）
     */
    @TableField(value = "bunk_beds")
    private Integer bunkBeds;

    /**
     * 彩条布（包）
     */
    @TableField(value = "tarpaulins")
    private Integer tarpaulins;

    /**
     * 防潮垫（张）
     */
    @TableField(value = "moisture_proof_pads")
    private Integer moistureProofPads;

    /**
     * 发电机（台）
     */
    @TableField(value = "generators")
    private Integer generators;

    /**
     * 照明灯具（个）
     */
    @TableField(value = "lighting_fixtures")
    private Integer lightingFixtures;

    /**
     * 照明灯组（套）
     */
    @TableField(value = "lighting_sets")
    private Integer lightingSets;

    /**
     * 手电筒（支）
     */
    @TableField(value = "flashlights")
    private Integer flashlights;

    /**
     * 雨衣（件）
     */
    @TableField(value = "raincoats")
    private Integer raincoats;

    /**
     * 雨靴（双）
     */
    @TableField(value = "rain_boots")
    private Integer rainBoots;

    /**
     * 其他装备数量（个）
     */
    @TableField(value = "other_equipment")
    private Integer otherEquipment;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

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

    // 解析 geom 字段，将其拆分为经纬度
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
    @TableField(value = "insert_time")
    private Date insertTime;

    /**
     * 其他
     */
    @TableField(value = "other")
    private String other;

    /**
     * 赈济家庭箱
     */
    @TableField(value = "relief_families_box")
    private Integer reliefFamiliesBox;
}
