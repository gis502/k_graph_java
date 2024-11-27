package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEventGetPageVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 0:41
 * @description: 返回结果dto
 */


@Data
@Builder
public class ResultEventGetPageDTO {

    private int code;
    private String msg;
    private Data data;


    @JsonCreator
    public ResultEventGetPageDTO(@JsonProperty("code") int code, @JsonProperty("msg") String msg, @JsonProperty("data") Data data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    @lombok.Data
    public static class Data {
        @JsonProperty("total")
        private int total;
        @JsonProperty("code")
        private int code;
        @JsonProperty("msg")
        private Object msg;
        @JsonProperty("rows")
        private List<ResultEventGetPageVO> rows;

    }
}
