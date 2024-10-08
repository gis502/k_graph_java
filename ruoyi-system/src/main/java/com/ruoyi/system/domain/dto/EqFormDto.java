package com.ruoyi.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class EqFormDto {

    @NotEmpty(message = "地震名称不能为空")
    private String earthquakeName;

    @NotNull(message = "开始时间不能为空")
    private String startTime;

    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @NotNull(message = "起始震级不能为空")
    @Pattern(regexp = "^[0-9]*\\.?[0-9]+$", message = "震级必须为数字")
    private String startMagnitude;

    @NotNull(message = "结束震级不能为空")
    @Pattern(regexp = "^[0-9]*\\.?[0-9]+$", message = "震级必须为数字")
    private String endMagnitude;

    @NotNull(message = "起始深度不能为空")
    @Pattern(regexp = "^[0-9]*\\.?[0-9]+$", message = "深度必须为数字")
    private String startDepth;

    @NotNull(message = "结束深度不能为空")
    @Pattern(regexp = "^[0-9]*\\.?[0-9]+$", message = "深度必须为数字")
    private String endDepth;
}
