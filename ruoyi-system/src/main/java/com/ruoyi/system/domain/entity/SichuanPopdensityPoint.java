package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName(value = "sichuan_popdensity_point")
public class SichuanPopdensityPoint {
    @TableId(value = "gid", type = IdType.NONE)
    private Integer gid;

    @TableField(value = "density")
    private BigDecimal density;

    @TableField(value = "area")
    private BigDecimal area;

    @TableField(value = "perimeter")
    private BigDecimal perimeter;

    @TableField(value = "geom")
    private Object geom;

    @TableField(value = "geom_4326")
    private Object geom4326;
}