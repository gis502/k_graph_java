package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "list_column")
public class ListColumn {
    @TableField(value = "\"table_name\"")
    private Object tableName;

    @TableField(value = "\"column_name\"")
    private Object columnName;

    @TableField(value = "column_comment")
    private String columnComment;

    @TableField(value = "is_required")
    private Integer isRequired;

    @TableField(value = "is_pk")
    private Integer isPk;

    @TableField(value = "sort")
    private Short sort;

    @TableField(value = "is_increment")
    private Integer isIncrement;

    @TableField(value = "column_type")
    private String columnType;
}