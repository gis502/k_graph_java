package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-12-14 0:44
 * @description: 获取评估批次（需要进度条信息）
 */

@Data
public class ResultEventGetBatchVO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("event")
    private String event;

    @JsonProperty("batch")
    private Integer batch;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("state")
    private Integer state;

    @JsonProperty("beginTime")
    private String beginTime;

    @JsonProperty("endTime")
    private String endTime;

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("remark")
    private String remark;



}
