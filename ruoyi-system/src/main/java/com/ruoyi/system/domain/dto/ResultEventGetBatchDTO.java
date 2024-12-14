package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.system.domain.vo.ResultEventGetBatchVO;
import com.ruoyi.system.domain.vo.ResultEventGetMapVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: xiaodemos
 * @date: 2024-12-14 0:41
 * @description: 获取评估批次DTO（需要进度条信息）
 */

@Data
@Builder
public class ResultEventGetBatchDTO {

    private int code;
    private String msg;
    private List<ResultEventGetBatchVO> data;

    @JsonCreator
    public ResultEventGetBatchDTO(
            @JsonProperty("code") int code,
            @JsonProperty("msg") String msg,
            @JsonProperty("data") List<ResultEventGetBatchVO> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;

    }

}
