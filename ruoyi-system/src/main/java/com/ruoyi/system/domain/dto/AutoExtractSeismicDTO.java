package com.ruoyi.system.domain.dto;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiaodemos
 * @date: 2025-04-25 10:53
 * @description:
 */


@Data
public class AutoExtractSeismicDTO implements Serializable {

    private int depth;
    private String  time;
    private double latitude;
    private double longitude;
    private String location;
    private double magnitude;

}
