package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.time.LocalDateTime;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 9:27
 * @description: 地震触发时保存的数据表
 */

@Data
@Builder
@TableName("eqlist")
public class EqList {
    // 地震唯一标识符
    @TableId(value = "eqid" )
    private String eqid;
    // 队列唯一标识符
    @TableField(value = "eqqueue_id")
    private String eqqueueId;
    // 地震名称
    @TableField(value = "earthquake_name")
    private String earthquakeName;
    // 地震全称
    @TableField(value = "earthquake_full_name")
    private String earthquakeFullName;
    // 震中位置
    @TableField(value = "eq_addr")
    private String eqAddr;
    // 震中经纬度（需要导入相应的Geometry类）
    @TableField(value = "geom")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)  // 仅序列化非空字段
    private Geometry geom;
    // 地震烈度
    @TableField(value = "intensity")
    private String intensity;
    // 震级
    @TableField(value = "magnitude")
    private String magnitude;
    // 震源深度
    @TableField(value = "depth")
    private String depth;
    // 地震发生时间
    @TableField(value = "occurrence_time")
    private LocalDateTime occurrenceTime;
    // 地震类型（Z正式，Y演练，T测试）
    @TableField(value = "eq_type")
    private String eqType;
    // 来源（0:12322、1:手动触发）
    @TableField(value = "source")
    private String source;
    // 震中所在县编码
    @TableField(value = "eq_addr_code")
    private String eqAddrCode;
    // 震中所在乡镇编码
    @TableField(value = "town_code")
    private String townCode;
    // 行政区代码（以pac模糊查询），默认51
    @TableField(value = "pac")
    private String pac;
    // 地震类型(CC/CD/AU/...),多参数英文半角逗号(,)分割
    @TableField(value = "type")
    private String type;
    @TableField(value = "is_deleted")
    private Integer isDeleted;



}
