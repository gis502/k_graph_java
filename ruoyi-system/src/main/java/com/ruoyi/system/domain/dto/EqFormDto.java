package com.ruoyi.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class EqFormDto {


    private String earthquakeName;


    private String startTime;


    private String endTime;



    private String startMagnitude;



    private String endMagnitude;



    private String startDepth;



    private String endDepth;
}
