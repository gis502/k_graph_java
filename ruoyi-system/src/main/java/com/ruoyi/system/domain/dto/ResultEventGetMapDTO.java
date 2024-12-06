package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import com.ruoyi.system.domain.vo.ResultSeismicEventGetResultVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 17:16
 * @description: 专题图返回结果DTO
 */

@Data
@Builder
public class ResultEventGetMapDTO {

    private int code;
    private String msg;
    private List<ResultEventGetMapVO> data;

    @JsonCreator
    public ResultEventGetMapDTO(
            @JsonProperty("code") int code,
            @JsonProperty("msg") String msg,
            @JsonProperty("data") List<ResultEventGetMapVO> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;

    }


}
