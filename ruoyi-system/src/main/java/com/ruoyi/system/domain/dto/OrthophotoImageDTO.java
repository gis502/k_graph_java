package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public class OrthophotoImageDTO {
    private String name;
    private String path;
    private String height;
    private String createTime;
    private BigDecimal angle;
}
