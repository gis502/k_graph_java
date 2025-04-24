package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: xiaodemos
 * @date: 2025-04-24 14:51
 * @description: 词云图
 */


@Data
@TableName("cloudwords")
public class CloudWords {

    @TableField("eqid")
    private String eqid;
    @TableField("eqqueue_id")
    private String eqqueueId;
    @TableField("result")
    private String result;

}
