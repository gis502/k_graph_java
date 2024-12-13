package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @date: 2024-12-12 23:37
 * @description: geometry对象转换成经度纬度
 */


@Data
@Builder
public class ResultEqListDTO {

    private String eqId;
    // 队列唯一标识符
    private String eqqueueId;
    // 地震名称
    private String earthquakeName;
    // 地震全称
    private String earthquakeFullName;
    // 震中位置
    private String eqAddr;
    // 震级
    private String magnitude;
    // 地震烈度
    private String intensity;
    // 震源深度
    private String depth;
    // 地震发生时间
    private String occurrenceTime;
    // 地震类型（Z正式，Y演练，T测试）
    private String eqType;
    // 来源（0:12322、1:手动触发）
    private String source;
    // 震中所在县编码
    private String eqAddrCode;
    // 震中所在乡镇编码
    private String townCode;
    // 行政区代码（以pac模糊查询），默认51
    private String pac;
    // 地震类型(CC/CD/AU/...),多参数英文半角逗号(,)分割
    private String type;

    private Double longitude;

    private Double latitude;

}
