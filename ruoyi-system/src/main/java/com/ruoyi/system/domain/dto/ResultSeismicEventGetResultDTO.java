package com.ruoyi.system.domain.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultSeismicEventGetResultVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 1:29
 * @description: 返回结果经济建筑人员伤亡评估dto
 */

@Data
@Builder
public class ResultSeismicEventGetResultDTO {

    private int code;
    private String msg;
    private ResultSeismicEventGetResultVO data;

    @JsonCreator
    public ResultSeismicEventGetResultDTO(
            @JsonProperty("code") int code,
            @JsonProperty("msg") String msg,
            @JsonProperty("data") ResultSeismicEventGetResultVO data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

}
