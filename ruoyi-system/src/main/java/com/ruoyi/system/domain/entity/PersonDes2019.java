package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "person_des_2019")
public class PersonDes2019 {
    @TableField(value = "geom")
    private Object geom;

    @TableField(value = "uuid")
    private Object uuid;

    @TableField(value = "peopledes")
    private Integer peopledes;
}