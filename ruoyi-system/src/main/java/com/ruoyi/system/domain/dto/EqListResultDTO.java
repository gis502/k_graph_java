package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEqListVO;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2025-04-19 22:11
 * @description: 同步最新地震
 */

@Data
public class EqListResultDTO {


    private int code;
    private String msg;
    private ResultEqListVO data;


    @JsonCreator
    public EqListResultDTO(@JsonProperty("code") int code, @JsonProperty("msg") String msg, @JsonProperty("data") ResultEqListVO data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


}
