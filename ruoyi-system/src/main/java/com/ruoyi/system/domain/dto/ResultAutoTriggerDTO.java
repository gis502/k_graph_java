package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultAutoTriggerVO;
import com.ruoyi.system.domain.vo.ResultEventGetResultTownVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2025-01-09 17:50
 * @description: 自动触发返回结果类
 */

@Data
@Builder
public class ResultAutoTriggerDTO {

    private String msg;
    private int code;

    private List<ResultAutoTriggerVO> data;

    @JsonCreator
    public ResultAutoTriggerDTO(@JsonProperty("msg") String msg,
                                @JsonProperty("code") int code,
                                @JsonProperty("data") List<ResultAutoTriggerVO> data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }



}
