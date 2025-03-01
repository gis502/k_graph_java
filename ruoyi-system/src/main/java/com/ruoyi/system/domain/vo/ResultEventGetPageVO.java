package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 4:40
 * @description: 地震事件VO对象
 */


@Data
public class ResultEventGetPageVO {

    @JsonProperty("event")
    private String event;
    @JsonProperty("eqName")
    private String eqName;
    @JsonProperty("eqFullName")
    private String eqFullName;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("eqDepth")
    private Double eqDepth;
    @JsonProperty("eqTime")
    private String eqTime;
    @JsonProperty("eqMagnitude")
    private String eqMagnitude;
    @JsonProperty("eqAddr")
    private String eqAddr;
    @JsonProperty("eqType")
    private String eqType;
    @JsonProperty("eqAddrCode")
    private String eqAddrCode;
    @JsonProperty("townCode")
    private String townCode;
    @JsonProperty("source")
    private String source;
    @JsonProperty("createTime")
    private String createTime;

}
