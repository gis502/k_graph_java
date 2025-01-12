package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2025-01-09 17:51
 * @description: 自动触发结果字段类
 */

@Data
public class ResultAutoTriggerVO {

    @JsonProperty("eqid")
    private String eqid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("place")
    private String place;

    @JsonProperty("eqtime")
    private String eqtime;

    @JsonProperty("m")
    private Double m;

    @JsonProperty("lon")
    private Double lon;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("depth")
    private Double depth;

    @JsonProperty("pac")
    private String pac;

    @JsonProperty("type")
    private String type;

    @JsonProperty("createTime")
    private String createTime;

}
