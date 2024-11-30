package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEventGetResultTownVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 2:49
 * @description: 乡镇级评估结果返回dto
 */

@Data
@Builder
public class ResultEventGetResultTownDTO {

    private String msg;
    private int code;

    private List<ResultEventGetResultTownVO> data;

    @JsonCreator
    public ResultEventGetResultTownDTO(@JsonProperty("msg") String msg, @JsonProperty("code") int code, @JsonProperty("data") List<ResultEventGetResultTownVO> data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }


}
