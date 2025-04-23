package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 4:40
 * @description: 地震事件VO对象
 */


@Data
public class ResultEqListVO {

    @JsonProperty("eqId")
    private String eqId;

    @JsonProperty("eqqueueId")
    private String eqqueueId;

    @JsonProperty("eqName")
    private String eqName;

    @JsonProperty("eqTime")
    private String eqTime;

    @JsonProperty("eqDepth")
    private Double eqDepth;

    @JsonProperty("eqAddr")
    private String eqAddr;

    @JsonProperty("eqType")
    private String eqType;

    @JsonProperty("magnitude")
    private Double magnitude;

    @JsonProperty("intensity")
    private Integer intensity;

    @JsonProperty("eqFullName")
    private String eqFullName;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("fullName")
    private String fullName;

}
