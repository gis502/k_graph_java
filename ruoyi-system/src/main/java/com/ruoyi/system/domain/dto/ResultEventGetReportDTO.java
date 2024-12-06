package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import com.ruoyi.system.domain.vo.ResultEventGetReportVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 23:57
 * @description: 返回灾情报告结果DTO类
 */


@Data
@Builder
public class ResultEventGetReportDTO {

    private int code;
    private String msg;
    private List<ResultEventGetReportVO> data;

    @JsonCreator
    public ResultEventGetReportDTO(
            @JsonProperty("code") int code,
            @JsonProperty("msg") String msg,
            @JsonProperty("data") List<ResultEventGetReportVO> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;

    }








}
