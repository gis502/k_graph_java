package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 17:17
 * @description: 专题图返回结果VO对象
 */

@Data
public class ResultEventGetMapVO {

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
    @JsonProperty("size")
    private String size;

}
