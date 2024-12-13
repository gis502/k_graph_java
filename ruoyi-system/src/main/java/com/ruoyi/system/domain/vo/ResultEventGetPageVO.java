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
    @JsonProperty("eqTime")
    private String eqTime;
    @JsonProperty("eqAddr")
    private String eqAddr;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("eqMagnitude")
    private String eqMagnitude;
    @JsonProperty("eqDepth")
    private Double eqDepth;
    @JsonProperty("eqFullName")
    private String eqFullName;
    @JsonProperty("eqName")
    private String eqName;
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
