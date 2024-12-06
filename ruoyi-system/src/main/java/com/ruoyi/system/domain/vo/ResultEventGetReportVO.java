package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 23:55
 * @description: 返回灾情报告结果VO对象
 */

@Data
public class ResultEventGetReportVO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("eqqueueId")
    private String eqqueueId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("proTime")
    private String proTime;
    @JsonProperty("fileType")
    private String fileType;
    @JsonProperty("fileName")
    private String fileName;
    @JsonProperty("fileExtension")
    private String fileExtension;
    @JsonProperty("fileSize")
    private Double fileSize;
    @JsonProperty("sourceFile")
    private String sourceFile;
    @JsonProperty("remark")
    private String remark;

}
